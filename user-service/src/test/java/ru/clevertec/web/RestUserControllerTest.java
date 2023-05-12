package ru.clevertec.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.security.utils.JwtValidator;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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
public class RestUserControllerTest {

    private static final String PATH_TO_USER_JSON = "src/test/resources/json/user.json";
    private static final String PATH_TO_USER_CREATE_JSON = "src/test/resources/json/user_create.json";
    private static final String PATH_TO_USER_UPDATE_JSON = "src/test/resources/json/user_update.json";
    private static final String LOCALHOST = "localhost";
    private static final String BASE_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RestUserController controller;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JwtValidator validator;
    private static WireMockServer server;

    @BeforeAll
    static void beforeAll() {
        WireMockConfiguration config = new WireMockConfiguration();
        config.port(8081);
        server = new WireMockServer(config);
    }

    @BeforeEach
    void setUp() {
        server.start();
        configureFor(LOCALHOST, 8081);
        doReturn(true).when(validator).validateAccessToken(any());
        Claims claims = Jwts.claims();
        claims.put("id", 1L);
        claims.put("role", "ADMIN");
        claims.put("email", "johnson@gmail.us");
        doReturn(claims).when(validator).getAccessClaims(any());
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    public void checkFindByIdShouldReturnUserAndStatus200() throws Exception {
        List<UserReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), new TypeReference<>() {
        });
        UserReadDto user = list.get(0);
        String userStr = OBJECT_MAPPER.writeValueAsString(user);
        stubFor(get(urlEqualTo(BASE_URL + "/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(userStr)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1L).header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Nick")));
    }

    @Test
    public void checkGetByIdShouldReturnErrorMessageAndStatus404() throws Exception {
        stubFor(get(urlEqualTo(BASE_URL + "/3"))
                .willReturn(aResponse()
                        .withBody("""
                                {
                                   "errorType":"Client error",
                                   "errorMessage":"Not found user with id = 1"
                                }
                                """
                        )
                        .withStatus(404)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 3L).header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.errorType", is("Client error")))
                .andExpect(jsonPath("$.errorMessage", is("Not found user with id = 1")));
    }

    @Test
    public void checkGetAllShouldReturnTwoUsersAndStatus200() throws Exception {
        List<UserReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), new TypeReference<>() {
        });
        String body = OBJECT_MAPPER.writeValueAsString(list);
        stubFor(get(urlEqualTo(BASE_URL + "?page=1&size=2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .withStatus(200)));
        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "?page=1&size=2").header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(list.size())));
    }

    @Test
    public void checkFindByEmailShouldReturnUserAndStatus200() throws Exception {
        List<UserReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), new TypeReference<>() {
        });
        UserReadDto user = list.get(0);
        String userStr = OBJECT_MAPPER.writeValueAsString(user);
        stubFor(get(urlEqualTo(BASE_URL + "/params?email=johnson%40gmail.us"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(userStr)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/params?email=johnson@gmail.us").header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Nick")));
    }

    @Test
    public void checkCreateShouldReturnUserAndStatus201() throws Exception {
        ClientUserCreateDto clientUserCreateDto = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_CREATE_JSON), ClientUserCreateDto.class);
        String requestBody = OBJECT_MAPPER.writeValueAsString(clientUserCreateDto);
        List<UserReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), new TypeReference<>() {
        });
        UserReadDto userReadDto = list.get(2);
        String response = OBJECT_MAPPER.writeValueAsString(userReadDto);

        stubFor(post(urlEqualTo(BASE_URL))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.role", is("SUBSCRIBER")));
    }

    @Test
    public void checkCreateShouldThrowSuchEntityExistsExc() throws Exception {
        ClientUserCreateDto clientUserCreateDto = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_CREATE_JSON), ClientUserCreateDto.class);
        String requestBody = OBJECT_MAPPER.writeValueAsString(clientUserCreateDto);

        stubFor(post(urlEqualTo(BASE_URL))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withBody("""
                                {
                                   "errorType":"Client error",
                                   "errorMessage":"Already exists user with email andrpetrov@yandex.ru"
                                }
                                """
                        )));

        mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.errorType", is("Client error")))
                .andExpect(jsonPath("$.errorMessage", is("Already exists user with email andrpetrov@yandex.ru")));
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void checkUpdateShouldReturnUserAndStatus200() throws Exception {
        ClientUserUpdateDto clientUserUpdateDto = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_UPDATE_JSON), ClientUserUpdateDto.class);
        String requestBody = OBJECT_MAPPER.writeValueAsString(clientUserUpdateDto);
        List<UserReadDto> list = OBJECT_MAPPER.readValue(new File(PATH_TO_USER_JSON), new TypeReference<>() {
        });
        UserReadDto userReadDto = list.get(2);
        userReadDto.setRole(UserRole.JOURNALIST);
        String response = OBJECT_MAPPER.writeValueAsString(userReadDto);

        stubFor(put(urlEqualTo(BASE_URL + "/3"))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        mvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 3L)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.role", is("JOURNALIST")));
    }

    @Test
    public void checkDeleteShouldReturnStatus204() throws Exception {
        stubFor(delete(urlEqualTo(BASE_URL + "/3"))
                .willReturn(aResponse()
                        .withStatus(204)));

        mvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 3L)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNoContent());
    }
}

