package ru.clevertec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.NewsUpdateDto;
import ru.clevertec.client.dto.SimpleNewsReadDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.security.utils.JwtValidator;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.QueryParamsNews;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class RestNewsControllerTest {

    private static final String LOCALHOST = "localhost";
    private static final String BASE_NEWS_URL = "/v1/news";
    private static final String BASE_USER_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final int USER_PORT = 8081;
    private static final int NEWS_PORT = 8082;
    private static final String EMAIL = "email@email.com";
    private static final String TEXT = "text";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TITLE = "title";
    private static WireMockServer userServer;
    private static WireMockServer newsServer;
    @Autowired
    private RestCommentController controller;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JwtValidator validator;

    @BeforeAll
    static void beforeAll() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        WireMockConfiguration userServerConfig = new WireMockConfiguration();
        userServerConfig.containerThreads(50);
        userServerConfig.port(USER_PORT);
        userServer = new WireMockServer(userServerConfig);

        WireMockConfiguration newsServerConfig = new WireMockConfiguration();
        newsServerConfig.containerThreads(50);
        newsServerConfig.port(NEWS_PORT);
        newsServer = new WireMockServer(newsServerConfig);
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        userServer.start();
        newsServer.start();
        Thread.sleep(500);
        doReturn(true).when(validator).validateAccessToken(any());
        Claims claims = Jwts.claims();
        claims.put("id", 1L);
        claims.put("role", "ADMIN");
        claims.put("email", "johnson@gmail.us");
        doReturn(claims).when(validator).getAccessClaims(any());
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        userServer.stop();
        newsServer.stop();
        Thread.sleep(500);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void checkGetAllShouldHasSize2AndStatus200() throws Exception {
        List<SimpleNewsReadDto> list = getListSimpleNewsReadDto();
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_NEWS_URL))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(list))));


        List<Long> usersIds = Arrays.asList(1L, 1L);
        List<UserDto> userDtoList = Arrays.asList(getUserDto(), getUserDto());
        configureFor(LOCALHOST, USER_PORT);
        stubFor(put(urlPathEqualTo(BASE_USER_URL + "/ids"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(usersIds)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userDtoList))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_NEWS_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void checkGetByIdShouldHasCommentSize2AndStatus200() throws Exception {
        NewsReadDto newsReadDto = getNewsReadDto();
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_NEWS_URL + "/1"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(newsReadDto))));


        List<Long> usersIds = Arrays.asList(1L, 1L, 1L);
        List<UserDto> userDtoList = Arrays.asList(getUserDto(), getUserDto());
        configureFor(LOCALHOST, USER_PORT);
        stubFor(put(urlPathEqualTo(BASE_USER_URL + "/ids"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(usersIds)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userDtoList))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_NEWS_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.comments", hasSize(2)));

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void checkGetByParamsShouldHasSize2AndStatus200() throws Exception {
        List<SimpleNewsReadDto> list = getListSimpleNewsReadDto();
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_NEWS_URL + "/params"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .withQueryParam("user_id", equalTo("1"))
                .withQueryParam("title", equalTo(TITLE))
                .withQueryParam("text", equalTo(TEXT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(list))));


        List<Long> usersIds = Arrays.asList(1L, 1L);
        List<UserDto> userDtoList = Arrays.asList(getUserDto(), getUserDto());
        configureFor(LOCALHOST, USER_PORT);
        stubFor(put(urlPathEqualTo(BASE_USER_URL + "/ids"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(usersIds)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userDtoList))));

        QueryParamsNews params = new QueryParamsNews();
        params.setUser_id(1L);
        params.setTitle(TITLE);
        params.setText(TEXT);

        mvc.perform(MockMvcRequestBuilders.get(BASE_NEWS_URL + "/params")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void checkUpdateShouldAuthorLastNameEqualsAndStatus200() throws Exception {
        configureFor(LOCALHOST, USER_PORT);
        stubFor(get(urlPathEqualTo(BASE_USER_URL + "/params"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getUserDto()))));

        List<Long> usersIds = Arrays.asList(1L, 1L, 1L);
        List<UserDto> userDtoList = Arrays.asList(getUserDto(), getUserDto());
        stubFor(put(urlPathEqualTo(BASE_USER_URL + "/ids"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(usersIds)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userDtoList))));


        NewsUpdateDto newsUpdateDto = getNewsUpdateDto();
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(put(urlPathEqualTo(BASE_NEWS_URL + "/1"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(newsUpdateDto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getNewsReadDto()))));

        NewsReadDto newsReadDto = getNewsReadDto();
        stubFor(get(urlPathEqualTo(BASE_NEWS_URL + "/1"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(newsReadDto))));

        ClientNewsUpdateDto clientNewsUpdateDto = getClientNewsUpdateDto();
        mvc.perform(MockMvcRequestBuilders.put(BASE_NEWS_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(clientNewsUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.author.lastName", is(LAST_NAME)));
    }

    @Test
    void checkUpdateShouldThrowBadRequestExcAndStatus400() throws Exception {
        ClientNewsUpdateDto clientNewsUpdateDto = getClientNewsUpdateDto();
        mvc.perform(MockMvcRequestBuilders.put(BASE_NEWS_URL + "/2")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(clientNewsUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkCreateShouldAuthorLastNameEqualsAndStatus201() throws Exception {
        configureFor(LOCALHOST, USER_PORT);
        stubFor(get(urlPathEqualTo(BASE_USER_URL + "/params"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getUserDto()))));


        NewsCreateDto newsCreateDto = getNewsCreateDto();
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(post(urlPathEqualTo(BASE_NEWS_URL))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(newsCreateDto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getNewsReadDto()))));


        ClientNewsCreateDto createDto = getClientNewsCreateDto();
        mvc.perform(MockMvcRequestBuilders.post(BASE_NEWS_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.author.lastName", is(LAST_NAME)));
    }

    @Test
    void checkCreateShouldThrowValidationExcAndStatus422() throws Exception {
        ClientNewsCreateDto createDto = getClientNewsCreateDto();
        createDto.setEmail("invalidEmail");
        mvc.perform(MockMvcRequestBuilders.post(BASE_NEWS_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(createDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkDeleteByIdShouldStatus204() throws Exception {
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_NEWS_URL + "/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getNewsReadDto()))));

        stubFor(delete(urlEqualTo(BASE_NEWS_URL + "/1"))
                .willReturn(aResponse()
                        .withStatus(204)));


        mvc.perform(MockMvcRequestBuilders.delete(BASE_NEWS_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNoContent());
    }

    private ClientNewsCreateDto getClientNewsCreateDto() {
        ClientNewsCreateDto dto = new ClientNewsCreateDto();
        dto.setEmail(EMAIL);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        return dto;
    }

    private NewsCreateDto getNewsCreateDto() {
        NewsCreateDto dto = new NewsCreateDto();
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        return dto;
    }

    private ClientNewsUpdateDto getClientNewsUpdateDto() {
        ClientNewsUpdateDto dto = new ClientNewsUpdateDto();
        dto.setId(1L);
        dto.setEmail(EMAIL);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        return dto;
    }

    private NewsUpdateDto getNewsUpdateDto() {
        NewsUpdateDto dto = new NewsUpdateDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        return dto;
    }

    private NewsReadDto getNewsReadDto() {
        NewsReadDto dto = new NewsReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        dto.setTime(Instant.now());
        CommentReadDto comment1 = getCommentReadDto();
        CommentReadDto comment2 = getCommentReadDto();
        comment2.setId(2L);
        dto.setComments(Arrays.asList(comment1, comment2));
        return dto;
    }

    private List<SimpleNewsReadDto> getListSimpleNewsReadDto() {
        SimpleNewsReadDto news1 = getSimpleNewsReadDto();
        SimpleNewsReadDto news2 = getSimpleNewsReadDto();
        news2.setId(2L);
        return Arrays.asList(news1, news2);
    }

    private SimpleNewsReadDto getSimpleNewsReadDto() {
        SimpleNewsReadDto dto = new SimpleNewsReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        dto.setTime(Instant.now());
        return dto;
    }

    private UserDto getUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setUserRole(UserRole.SUBSCRIBER);
        return dto;
    }

    private CommentReadDto getCommentReadDto() {
        CommentReadDto dto = new CommentReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setText(TEXT);
        dto.setCreateTime(Instant.now());
        return dto;
    }
}
