package ru.clevertec.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.security.JwtValidator;
import ru.clevertec.service.dto.QueryParamsComment;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.hasSize;
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

    private static final String PATH_TO_CLIENT_COMMENT_READ_JSON = "src/test/resources/json/client_comment_read.json";
    private static final String PATH_TO_CLIENT_COMMENT_CREATE_JSON = "src/test/resources/json/client_comment_create.json";
    private static final String PATH_TO_SIMPLE_CLIENT_COMMENT_READ_JSON = "src/test/resources/json/simple_client_comment_read.json";
    private static final String PATH_TO_USER_JSON = "src/test/resources/json/user.json";
    private static final String PATH_TO_COMMENT_UPDATE = "src/test/resources/json/comment_update.json";
    private static final String LOCALHOST = "localhost";
    private static final String BASE_COMMENT_URL = "/v1/comments";
    private static final String BASE_USER_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int NEWS_PORT = 8082;
    private static final int USER_PORT = 8081;

    @Autowired
    private RestCommentController controller;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JwtValidator validator;
    private static WireMockServer userServer;
    private static WireMockServer newsServer;

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
    void setUp() {
        userServer.start();
        configureFor(LOCALHOST, USER_PORT);
        newsServer.start();
        configureFor(LOCALHOST, NEWS_PORT);

        doReturn(true).when(validator).validateAccessToken(any());
        Claims claims = Jwts.claims();
        claims.put("id", 1L);
        claims.put("role", "ADMIN");
        claims.put("email", "johnson@gmail.us");
        doReturn(claims).when(validator).getAccessClaims(any());
    }

    @AfterEach
    void tearDown() {
        userServer.stop();
        newsServer.stop();
    }

//    @Test
//    void checkCreateShouldReturnCommentAndStatus201() throws Exception {
//        ClientCommentCreateDto clientCommentCreateDto = OBJECT_MAPPER.readValue(new File(PATH_TO_COMMENT_CREATE_JSON), ClientCommentCreateDto.class);
//
//        CommentCreateDto commentCreateDto = new CommentCreateDto();
//        commentCreateDto.setUserId(1L);
//        commentCreateDto.setNewsId(clientCommentCreateDto.getNewsId());
//        commentCreateDto.setText(clientCommentCreateDto.getText());
//        String requestToNewsServer = OBJECT_MAPPER.writeValueAsString(commentCreateDto);
//
//        List<CommentReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_COMMENT_READ_JSON), new TypeReference<>() {});
//        CommentReadDto commentReadDto = list.get(0);
//        String responseFromNewsDataService = OBJECT_MAPPER.writeValueAsString(commentReadDto);
//
//        stubFor(post(urlEqualTo(BASE_COMMENT_URL))
//                .withPort(NEWS_PORT)
//                .withRequestBody(equalToJson(requestToNewsServer))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withStatus(201)
//                        .withBody(responseFromNewsDataService)));
//
//        UserDto user = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), UserDto.class);
//        String responseFromUserDataServer = OBJECT_MAPPER.writeValueAsString(user);
//
//        stubFor(get(urlEqualTo(BASE_USER_URL + "/params?email=fedorov%40gmail.com"))
//                .withPort(USER_PORT)
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromUserDataServer)));
//
//        String requestToController = OBJECT_MAPPER.writeValueAsString(clientCommentCreateDto);
//        mvc.perform(MockMvcRequestBuilders.post(BASE_COMMENT_URL)
//                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
//                        .contentType(APPLICATION_JSON)
//                        .content(requestToController))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.author.lastName", is("Fedorov")));
//    }

//    @Test
//    void update() throws Exception {
//        UserDto userDto = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), UserDto.class);
//        String responseFromUserDataServer = OBJECT_MAPPER.writeValueAsString(userDto);
//        stubFor(get(urlEqualTo(BASE_USER_URL + "/params?email=fedorov@gmail.com"))
//                .withPort(USER_PORT)
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromUserDataServer)));
//
//        ClientCommentUpdateDto clientCommentUpdateDto = OBJECT_MAPPER.readValue(new File(PATH_TO_COMMENT_UPDATE), ClientCommentUpdateDto.class);
//        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
//        commentUpdateDto.setId(clientCommentUpdateDto.getId());
//        commentUpdateDto.setUserId(1L);
//        commentUpdateDto.setNewsId(clientCommentUpdateDto.getNewsId());
//        commentUpdateDto.setText(clientCommentUpdateDto.getText());
//        String requestToNewsServer = OBJECT_MAPPER.writeValueAsString(commentUpdateDto);
//        List<CommentReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_SIMPLE_CLIENT_COMMENT_READ_JSON), new TypeReference<>(){});
//        CommentReadDto commentReadDto = list.get(0);
//        String responseFromNewsServer = OBJECT_MAPPER.writeValueAsString(commentReadDto);
//        stubFor(put(urlEqualTo(BASE_COMMENT_URL + "/1"))
//                .withPort(NEWS_PORT)
//                .withRequestBody(equalToJson(requestToNewsServer))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromNewsServer)));
//
//        String requestToController = OBJECT_MAPPER.writeValueAsString(clientCommentUpdateDto);
//        mvc.perform(MockMvcRequestBuilders.put(BASE_COMMENT_URL + "/1")
//                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
//                        .contentType(APPLICATION_JSON)
//                        .content(requestToController))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.author.lastName", is("Fedorov")));
//    }

    @Test
    void delete() throws Exception {
        List<CommentReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_SIMPLE_CLIENT_COMMENT_READ_JSON), new TypeReference<>() {
        });
        CommentReadDto commentReadDto = list.get(0);
        String responseFromNewsServer = OBJECT_MAPPER.writeValueAsString(commentReadDto);

        stubFor(WireMock.get(urlEqualTo(BASE_COMMENT_URL + "/1"))
                .withPort(NEWS_PORT)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseFromNewsServer)));

        stubFor(WireMock.delete(urlEqualTo(BASE_COMMENT_URL + "/1"))
                .withPort(NEWS_PORT)
                .willReturn(aResponse()
                        .withStatus(204)));


        mvc.perform(MockMvcRequestBuilders.delete(BASE_COMMENT_URL + "/1")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll() throws Exception {
        List<CommentReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_SIMPLE_CLIENT_COMMENT_READ_JSON), new TypeReference<>() {
        });
        String responseFromNewsServer = OBJECT_MAPPER.writeValueAsString(list);
        stubFor(get(urlEqualTo(BASE_COMMENT_URL + "?page=1&size=2"))
                .withPort(NEWS_PORT)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseFromNewsServer)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_COMMENT_URL + "?page=1&size=2").header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(list.size())));
    }

//    @Test
//    void getById() throws Exception {
//        CommentReadDto commentReadDto = OBJECT_MAPPER.readValue(new File(PATH_TO_COMMENT_READ_JSON), CommentReadDto.class);
//        String responseFromNewsServer = OBJECT_MAPPER.writeValueAsString(commentReadDto);
//        stubFor(get(urlEqualTo(BASE_COMMENT_URL + "/1"))
//                .withPort(NEWS_PORT)
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromNewsServer)));
//
//        UserDto userDto = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), UserDto.class);
//        String responseFromUserServer = OBJECT_MAPPER.writeValueAsString(userDto);
//        stubFor(get(urlEqualTo(BASE_USER_URL + "/1"))
//                .withPort(USER_PORT)
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromUserServer)));
//
//        mvc.perform(MockMvcRequestBuilders.get(BASE_COMMENT_URL + "/{id}", 1L).header(HEADER_AUTHORIZATION, STUB_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.author.lastName", is("Fedorov")));
//    }

//    @Test
//    void getByParams() throws Exception {
//        QueryParamsComment queryParamsComment = new QueryParamsComment();
//        queryParamsComment.setText("new comment");
//        queryParamsComment.setUser_id(1L);
//        queryParamsComment.setNews_id(1L);
//        String params = OBJECT_MAPPER.writeValueAsString(queryParamsComment);
//        List<CommentReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_SIMPLE_CLIENT_COMMENT_READ_JSON), new TypeReference<>() {
//        });
//        String responseFromNewsServer = OBJECT_MAPPER.writeValueAsString(list);
//
//        stubFor(get(urlEqualTo(BASE_COMMENT_URL + "/params?page=1&size=2"))
//                .withPort(NEWS_PORT)
//                .withQueryParam("news_id", equalTo("1"))
//                .withQueryParam("user_id", equalTo("1"))
//                .withQueryParam("text", equalTo("new comment"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromNewsServer)));
//
//        mvc.perform(MockMvcRequestBuilders.get(BASE_COMMENT_URL + "/params?page=1&size=2")
//                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
//                        .contentType(APPLICATION_JSON)
//                        .content(params))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(list.size())));
//    }
}