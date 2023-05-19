package ru.clevertec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import ru.clevertec.client.dto.CommentCreateDto;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.CommentUpdateDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.security.utils.JwtValidator;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.QueryParamsComment;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class RestCommentControllerTest {

    private static final String LOCALHOST = "localhost";
    private static final String BASE_COMMENT_URL = "/v1/comments";
    private static final String BASE_USER_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int USER_PORT = 8081;
    private static final int NEWS_PORT = 8082;
    private static final String EMAIL = "email@email.com";
    private static final String TEXT = "text";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
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
        userServerConfig.containerThreads(1000);
        userServerConfig.port(USER_PORT);
        userServer = new WireMockServer(userServerConfig);

        WireMockConfiguration newsServerConfig = new WireMockConfiguration();
        newsServerConfig.containerThreads(1000);
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
    void checkCreateShouldReturnCommentAndStatus201() throws Exception {
        configureFor(LOCALHOST, USER_PORT);
        stubFor(get(urlPathEqualTo(BASE_USER_URL + "/params"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getUserDto()))));


        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(post(urlPathEqualTo(BASE_COMMENT_URL))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(getCommentCreateDto())))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody(OBJECT_MAPPER.writeValueAsString(getCommentReadDto()))));


        mvc.perform(MockMvcRequestBuilders.post(BASE_COMMENT_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(getClientCommentCreateDto())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.author.lastName", is(LAST_NAME)));
    }

    @Test
    void checkCreateShouldThrowValidationExcAndStatus422() throws Exception {
        ClientCommentCreateDto dto = getClientCommentCreateDto();
        dto.setEmail("invalidEmail");

        mvc.perform(MockMvcRequestBuilders.post(BASE_COMMENT_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    void checkUpdateShouldReturnUpdatedAndStatus200() throws Exception {
        configureFor(LOCALHOST, USER_PORT);
        stubFor(get(urlPathEqualTo(BASE_USER_URL + "/params"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getUserDto()))));


        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(put(urlPathEqualTo(BASE_COMMENT_URL + "/1"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(getCommentUpdateDto())))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(OBJECT_MAPPER.writeValueAsString(getCommentReadDto()))));


        mvc.perform(MockMvcRequestBuilders.put(BASE_COMMENT_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(getClientCommentUpdateDto())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.author.lastName", is(LAST_NAME)));
    }

    @Test
    void checkUpdateShouldThrowBadRequestExcAndStatus400() throws Exception {
        ClientCommentUpdateDto dto = getClientCommentUpdateDto();
        dto.setId(2L);
        mvc.perform(MockMvcRequestBuilders.put(BASE_COMMENT_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkDeleteShouldStatus204() throws Exception {
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(WireMock.get(urlEqualTo(BASE_COMMENT_URL + "/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getCommentReadDto()))));

        stubFor(WireMock.delete(urlEqualTo(BASE_COMMENT_URL + "/1"))
                .willReturn(aResponse()
                        .withStatus(204)));


        mvc.perform(MockMvcRequestBuilders.delete(BASE_COMMENT_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void checkGetAllShouldHasSize2AndStatus200() throws Exception {
        List<CommentReadDto> list = getListCommentReadDto();

        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_COMMENT_URL))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(list))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_COMMENT_URL)
                        .param("page", "1")
                        .param("size", "2")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    void checkGetByIdShouldReturnCommentAndStatus200() throws Exception {
        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_COMMENT_URL + "/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getCommentReadDto()))));

        configureFor(LOCALHOST, USER_PORT);
        stubFor(get(urlPathEqualTo(BASE_USER_URL + "/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(getUserDto()))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_COMMENT_URL + "/{id}", 1L)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.author.lastName", is(LAST_NAME)));
    }

    @Test
    void checkGetByParamsShouldHasSize2AndStatus200() throws Exception {
        List<CommentReadDto> list = getListCommentReadDto();
        QueryParamsComment params = new QueryParamsComment();
        params.setNews_id(1L);
        params.setUser_id(1L);
        params.setText(TEXT);

        configureFor(LOCALHOST, NEWS_PORT);
        stubFor(get(urlPathEqualTo(BASE_COMMENT_URL + "/params"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .withQueryParam("user_id", equalTo("1"))
                .withQueryParam("news_id", equalTo("1"))
                .withQueryParam("text", equalTo(TEXT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(list))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_COMMENT_URL + "/params")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(params))
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private ClientCommentCreateDto getClientCommentCreateDto() {
        ClientCommentCreateDto dto = new ClientCommentCreateDto();
        dto.setNewsId(1L);
        dto.setEmail(EMAIL);
        dto.setText(TEXT);
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

    private CommentCreateDto getCommentCreateDto() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setUserId(1L);
        dto.setNewsId(1L);
        dto.setText(TEXT);
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

    private List<CommentReadDto> getListCommentReadDto() {
        CommentReadDto comment1 = getCommentReadDto();
        comment1.setId(1L);
        CommentReadDto comment2 = getCommentReadDto();
        comment2.setId(2L);
        return Arrays.asList(comment1, comment2);
    }

    private ClientCommentUpdateDto getClientCommentUpdateDto() {
        ClientCommentUpdateDto dto = new ClientCommentUpdateDto();
        dto.setId(1L);
        dto.setNewsId(1L);
        dto.setEmail(EMAIL);
        dto.setText(TEXT);
        return dto;
    }

    private CommentUpdateDto getCommentUpdateDto() {
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setNewsId(1L);
        dto.setText(TEXT);
        return dto;
    }
}