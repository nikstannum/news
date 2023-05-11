package ru.clevertec.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.security.utils.JwtValidator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsInRelativeOrder;
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

    private static final String PATH_TO_JSON = "src/test/resources/json/user.json";
    private static final String LOCALHOST = "localhost";
    private static final String BASE_URL = "/v1/users";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String STUB_TOKEN = "Bearer token";

    @Autowired
    private RestUserController controller;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private JwtValidator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WireMockServer server = new WireMockServer(8081);

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
        List<UserReadDto> list = objectMapper.readValue(new File(PATH_TO_JSON), new TypeReference<>() {
        });
        UserReadDto user = list.get(0);
        String userStr = objectMapper.writeValueAsString(user);
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
        stubFor(get(urlEqualTo(BASE_URL + "/1"))
                .willReturn(aResponse()
                        .withBody("""
                                {
                                   "errorType":"Client error",
                                   "errorMessage":"Not found user with id = 1"
                                }
                                """
                        )
                        .withStatus(404)));

        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1L).header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.errorType", is("Client error")))
                .andExpect(jsonPath("$.errorMessage", is("Not found user with id = 1")));
    }

    @Test
    public void checkGetAllShouldReturnTwoUsersAndStatus200() throws Exception {
        List<UserReadDto> list = objectMapper.readValue(new File(PATH_TO_JSON), new TypeReference<>() {
        });
        String body = objectMapper.writeValueAsString(list);
        stubFor(get(urlEqualTo(BASE_URL + "?page=1&size=2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .withStatus(200)));
        mvc.perform(MockMvcRequestBuilders.get(BASE_URL + "?page=1&size=2").header(HEADER_AUTHORIZATION, STUB_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }


}

