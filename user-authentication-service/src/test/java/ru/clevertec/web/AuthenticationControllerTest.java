package ru.clevertec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.client.entity.User;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.service.util.JwtProvider;
import ru.clevertec.service.dto.LoginDto;
import ru.clevertec.service.dto.UserDto;
import ru.clevertec.service.token.RefreshJwtToken;
import ru.clevertec.service.util.JwtValidator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
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
class AuthenticationControllerTest {
    private static final String LOCALHOST = "localhost";
    private static final String BASE_USER_URL = "/v1/users";
    private static final String BASE_SECURITY_URL = "/v1/security";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final long ID = 1L;
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static WireMockServer server;
    @Autowired
    private AuthenticationController controller;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JwtValidator validator;
    @MockBean
    private JwtProvider provider;
    @MockBean
    private PasswordEncoder encoder;

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

    private UserDto getStandardUserDto() {
        UserDto user = new UserDto();
        user.setId(ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.ADMIN);
        return user;
    }

    private User getStandardUser() {
        User user = new User();
        user.setId(ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.ADMIN);
        return user;
    }

    @Test
    void checkLoginShouldSuccessLoginAndAccessTokenEquals() throws Exception {
        UserDto userDto = getStandardUserDto();
        String responseFromClient = OBJECT_MAPPER.writeValueAsString(userDto);

        stubFor(post(urlPathEqualTo(BASE_USER_URL + "/secure"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseFromClient)));

        doReturn(true).when(encoder).matches(any(), any());

        User user = getStandardUser();
        doReturn(ACCESS_TOKEN).when(provider).generateAccessToken(user);
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(EMAIL);
        loginDto.setPassword(PASSWORD);
        String requestBody = OBJECT_MAPPER.writeValueAsString(loginDto);

        mvc.perform(MockMvcRequestBuilders.post(BASE_SECURITY_URL + "/login")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(ACCESS_TOKEN)));
    }

    @Test
    void checkLoginShouldThrowValidationExcAndStatus422() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(EMAIL);
        loginDto.setPassword("short");
        String requestBody = OBJECT_MAPPER.writeValueAsString(loginDto);

        mvc.perform(MockMvcRequestBuilders.post(BASE_SECURITY_URL + "/login")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkGetNewAccessTokenShouldReturnTokenAndSrarus200() throws Exception {
        doReturn(true).when(validator).validateRefreshToken(any());
        Claims claims = Jwts.claims();
        claims.setSubject(EMAIL);
        doReturn(claims).when(validator).getRefreshClaims(any());

        UserDto userDto = getStandardUserDto();
        String responseFromClient = OBJECT_MAPPER.writeValueAsString(userDto);

        stubFor(post(urlPathEqualTo(BASE_USER_URL + "/secure"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseFromClient)));

        User user = getStandardUser();
        doReturn(ACCESS_TOKEN).when(provider).generateAccessToken(user);


        RefreshJwtToken refreshJwtToken = new RefreshJwtToken();
        refreshJwtToken.setRefreshToken("refresh");
        String requestBody = OBJECT_MAPPER.writeValueAsString(refreshJwtToken);
        mvc.perform(MockMvcRequestBuilders.post(BASE_SECURITY_URL + "/token")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(ACCESS_TOKEN)));
    }

    @Test
    void checkGetNewRefreshTokenShouldReturnNewAccessAndRefreshTokensAndStatus200() throws Exception {
        doReturn(true).when(validator).validateRefreshToken(any());

        Claims claims = Jwts.claims();
        claims.setSubject(EMAIL);
        doReturn(claims).when(validator).getRefreshClaims(any());

        UserDto userDto = getStandardUserDto();
        String responseFromClient = OBJECT_MAPPER.writeValueAsString(userDto);

        stubFor(post(urlPathEqualTo(BASE_USER_URL + "/secure"))
                .withQueryParam("email", equalTo(EMAIL))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseFromClient)));

        User user = getStandardUser();
        doReturn(ACCESS_TOKEN).when(provider).generateAccessToken(user);
        doReturn(REFRESH_TOKEN).when(provider).generateRefreshToken(user);

        RefreshJwtToken refreshJwtToken = new RefreshJwtToken();
        refreshJwtToken.setRefreshToken("refresh");
        String requestBody = OBJECT_MAPPER.writeValueAsString(refreshJwtToken);

        mvc.perform(MockMvcRequestBuilders.post(BASE_SECURITY_URL + "/refresh")
                        .header(HEADER_AUTHORIZATION, STUB_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(ACCESS_TOKEN)))
                .andExpect(jsonPath("$.refreshToken", is(REFRESH_TOKEN)));
    }
}