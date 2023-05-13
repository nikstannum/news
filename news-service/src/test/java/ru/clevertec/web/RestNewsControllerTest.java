package ru.clevertec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.File;
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
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.security.JwtValidator;
import ru.clevertec.service.dto.ClientNewsCreateDto;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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

    private static final String PATH_TO_CLIENT_COMMENT_READ_JSON = "src/test/resources/json/client_comment_read.json";
    private static final String PATH_TO_CLIENT_COMMENT_CREATE_JSON = "src/test/resources/json/client_comment_create.json";
    private static final String PATH_TO_SIMPLE_CLIENT_COMMENT_READ_JSON = "src/test/resources/json/simple_client_comment_read.json";
    private static final String PATH_TO_USER_JSON = "src/test/resources/json/user.json";
    private static final String PATH_TO_COMMENT_UPDATE = "src/test/resources/json/comment_update.json";
    private static final String PATH_TO_SIMPLE_NEWS = "src/test/resources/json/simple_news.json";
    private static final String PATH_TO_CLIENT_NEWS_CREATE = "src/test/resources/json/client_news_create.json";
    private static final String PATH_TO_NEWS_READ = "src/test/resources/json/news_read.json";
    private static final String LOCALHOST = "localhost";
    private static final String BASE_NEWS_URL = "/v1/news";
    private static final String BASE_USER_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final int NEWS_PORT = 8082;
    private static final int USER_PORT = 8081;

    @Autowired
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
//    void getAll() throws Exception {
//
//        List<SimpleNewsReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_SIMPLE_NEWS), new TypeReference<>() {
//        });
//        String responseFromNewsServer = OBJECT_MAPPER.writeValueAsString(list);
//
//        stubFor(get(urlEqualTo(BASE_NEWS_URL + "?page=1&size=2"))
//                .withPort(NEWS_PORT)
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseFromNewsServer)));
//
//
//        UserDto userDto = new UserDto();
//        userDto.setId(1L);
//        userDto.setFirstName("Ivan");
//        userDto.setLastName("Ivanov");
//        userDto.setEmail("ivanov@gmail.com");
//        userDto.setUserRole(UserRole.ADMIN);
//
//        stubFor(put(urlEqualTo(BASE_USER_URL + "/ids"))
//                .withPort(USER_PORT)
//
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(OBJECT_MAPPER.writeValueAsString(userDto))));
//
//        mvc.perform(MockMvcRequestBuilders.get(BASE_NEWS_URL + "?page=1&size=2").header(HEADER_AUTHORIZATION, STUB_TOKEN))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(list.size())));
//
//    }

    @Test
    void getById() {
    }

    @Test
    void getByParams() {
    }

    @Test
    void update() {
    }

    @Test
    void create() throws Exception {
        UserDto user = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), UserDto.class);
        String userStr = OBJECT_MAPPER.writeValueAsString(user);
        stubFor(get(urlEqualTo(BASE_USER_URL + "/params?email=fedorov@gmail.com"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(userStr)));

        NewsCreateDto newsCreateDto = new NewsCreateDto();
        newsCreateDto.setUserId(1L);
        newsCreateDto.setText("text");
        newsCreateDto.setTitle("title");

        NewsReadDto newsReadDto = OBJECT_MAPPER.readValue(new File(PATH_TO_NEWS_READ), NewsReadDto.class);
        newsReadDto.setComments(null);

        stubFor(post(urlEqualTo(BASE_NEWS_URL))
                .withPort(NEWS_PORT)
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(newsCreateDto)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody(OBJECT_MAPPER.writeValueAsString(newsReadDto))));


        ClientNewsCreateDto dto = OBJECT_MAPPER.readValue(new File(PATH_TO_CLIENT_NEWS_CREATE), ClientNewsCreateDto.class);

        mvc.perform(MockMvcRequestBuilders.post(BASE_NEWS_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.author.lastName", is("Fedorov")));

    }

    @Test
    void deleteById() {
    }
}