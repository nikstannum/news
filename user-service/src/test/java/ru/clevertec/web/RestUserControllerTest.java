package ru.clevertec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.client.dto.UserCreateDto;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.client.dto.UserUpdateDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.security.utils.JwtValidator;
import ru.clevertec.service.dto.ClientUserCreateDto;

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
public class RestUserControllerTest {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    private static final String LOCALHOST = "localhost";
    private static final String BASE_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static WireMockServer server;
    @Autowired
    private RestUserController controller;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JwtValidator validator;
    @MockBean
    private PasswordEncoder encoder;

    @BeforeAll
    static void beforeAll() {
        WireMockConfiguration config = new WireMockConfiguration();
        config.containerThreads(50);
        config.port(8081);
        server = new WireMockServer(config);
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        server.start();
        Thread.sleep(500);
        doReturn(true).when(validator).validateAccessToken(any());
        doReturn(PASSWORD).when(encoder).encode(PASSWORD);
        Claims claims = Jwts.claims();
        claims.put("id", 1L);
        claims.put("role", "ADMIN");
        claims.put("email", "johnson@gmail.us");
        doReturn(claims).when(validator).getAccessClaims(any());
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        server.stop();
        Thread.sleep(500);
    }

    @Test
    public void checkFindByIdShouldReturnUserAndStatus200() throws Exception {
        UserReadDto userReadDto = getUserReadDto();
        configureFor(LOCALHOST, 8081);
        stubFor(get(urlPathEqualTo(BASE_URL + "/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userReadDto))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1L).header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)));
    }

    private UserReadDto getUserReadDto() {
        UserReadDto dto = new UserReadDto();
        dto.setId(1L);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setRole(UserRole.ADMIN);
        return dto;
    }

    @Test
    public void checkGetByIdShouldReturnErrorMessageAndStatus404() throws Exception {
        configureFor(LOCALHOST, 8081);
        stubFor(get(urlPathEqualTo(BASE_URL + "/5"))
                .willReturn(aResponse()
                        .withBody("""
                                {
                                   "errorType":"Client error",
                                   "errorMessage":"Not found user with id = 5"
                                }
                                """
                        )
                        .withStatus(404)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 5)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.errorType", is("Client error")))
                .andExpect(jsonPath("$.errorMessage", is("Not found user with id = 5")));
    }

    @Test
    public void checkGetAllShouldReturnTwoUsersAndStatus200() throws Exception {
        UserReadDto user1 = getUserReadDto();
        UserReadDto user2 = getUserReadDto();
        user2.setId(2L);
        List<UserReadDto> list = Arrays.asList(user1, user2);

        configureFor(LOCALHOST, 8081);
        stubFor(get(urlPathEqualTo(BASE_URL))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("size", equalTo("2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(list))
                        .withStatus(200)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("page", "1")
                        .param("size", "2")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(list.size())));
    }

    @Test
    public void checkFindByEmailShouldReturnUserAndStatus200() throws Exception {
        UserReadDto userReadDto = getUserReadDto();
        configureFor(LOCALHOST, 8081);
        stubFor(get(urlPathEqualTo(BASE_URL + "/params"))
                .withQueryParam("email", equalTo("johnson@gmail.us"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userReadDto))));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/params")
                        .param("email", "johnson@gmail.us")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)));
    }

    @Test
    public void checkCreateShouldReturnUserAndStatus201() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);

        UserReadDto userReadDto = getUserReadDto();

        configureFor(LOCALHOST, 8081);
        stubFor(post(urlPathEqualTo(BASE_URL))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(dto)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userReadDto))));

        mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    public void checkCreateShouldThrowSuchEntityExistsExc() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setPassword(PASSWORD);
        dto.setEmail(EMAIL);
        configureFor(LOCALHOST, 8081);
        stubFor(post(urlPathEqualTo(BASE_URL))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(dto)))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withBody("""
                                {
                                   "errorType":"Client error",
                                   "errorMessage":"Already exists user with email andrpetrov@yandex.ru"
                                }
                                """
                        )));

        ClientUserCreateDto clientUserCreateDto = new ClientUserCreateDto();
        clientUserCreateDto.setFirstName(FIRST_NAME);
        clientUserCreateDto.setLastName(LAST_NAME);
        clientUserCreateDto.setEmail(EMAIL);
        clientUserCreateDto.setPassword(PASSWORD);
        mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(clientUserCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.errorType", is("Client error")))
                .andExpect(jsonPath("$.errorMessage", is("Already exists user with email andrpetrov@yandex.ru")));
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void checkUpdateShouldReturnUserAndStatus200() throws Exception {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1L);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setRole(UserRole.ADMIN);

        UserReadDto userReadDto = getUserReadDto();

        configureFor(LOCALHOST, 8081);
        stubFor(put(urlPathEqualTo(BASE_URL + "/1"))
                .withRequestBody(equalToJson(OBJECT_MAPPER.writeValueAsString(dto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OBJECT_MAPPER.writeValueAsString(userReadDto))));

        mvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 1)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    public void checkDeleteShouldReturnStatus204() throws Exception {
        configureFor(LOCALHOST, 8081);
        stubFor(delete(urlEqualTo(BASE_URL + "/3"))
                .willReturn(aResponse()
                        .withStatus(204)));

        mvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 3L)
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNoContent());
    }
}

