package ru.clevertec.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.api.dto.UserCreateDto;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.api.dto.UserSecureDto;
import ru.clevertec.api.dto.UserUpdateDto;
import ru.clevertec.data.User.UserRole;
import ru.clevertec.service.UserService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password";
    private static final String BASE_URL = "/v1/users";
    private static final String ADMIN = "ADMIN";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService service;


    @Test
    public void checkFindByIdShouldReturnUserAndStatus200() throws Exception {
        UserReadDto userReadDto = prepareUserReadDto();
        Mockito.doReturn(userReadDto).when(service).findById(1L);
        mvc.perform(get(BASE_URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.role", is(ADMIN)));
    }

    private UserReadDto prepareUserReadDto() {
        UserReadDto user = new UserReadDto();
        user.setId(1L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setRole(UserRole.ADMIN);
        return user;

    }

    @Test
    void checkCreateShouldReturnUserAndStatus201() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName(FIRST_NAME);
        userCreateDto.setLastName(LAST_NAME);
        userCreateDto.setEmail(EMAIL);
        userCreateDto.setPassword(PASSWORD);

        UserReadDto userReadDto = prepareUserReadDto();
        Mockito.doReturn(userReadDto).when(service).create(Mockito.any());

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.role", is(ADMIN)));
    }

    @Test
    void checkCreateShouldThrowValidationExc() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName(FIRST_NAME);
        userCreateDto.setLastName(LAST_NAME);
        userCreateDto.setEmail(EMAIL);
        userCreateDto.setPassword("short");

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkFindAllShouldReturnSize2AndStatus200() throws Exception {
        UserReadDto user1 = prepareUserReadDto();
        UserReadDto user2 = prepareUserReadDto();
        List<UserReadDto> list = List.of(user1, user2);
        Mockito.doReturn(list).when(service).findAll(1, 2);
        mvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void checkFindUsersByIdsShouldReturnSize2AndStatus200() throws Exception {
        UserReadDto user1 = prepareUserReadDto();
        UserReadDto user2 = prepareUserReadDto();
        List<UserReadDto> list = List.of(user1, user2);
        List<Long> ids = List.of(1L, 2L);
        Mockito.doReturn(list).when(service).findUsersByIds(ids);
        mvc.perform(put(BASE_URL + "/ids")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void checkFindUserByEmailShouldReturnStatus200() throws Exception {
        UserReadDto user = new UserReadDto();
        user.setId(1L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setRole(UserRole.ADMIN);
        Mockito.doReturn(user).when(service).findByEmail(EMAIL);
        mvc.perform(get(BASE_URL + "/params")
                        .param("email", EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is(EMAIL)));
    }

    @Test
    void checkUpdateShouldSuccessAndStatus200() throws Exception {
        UserUpdateDto user = new UserUpdateDto();
        user.setId(1L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.ADMIN);

        UserReadDto userReadDto = prepareUserReadDto();
        Mockito.doReturn(userReadDto).when(service).update(Mockito.any());

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.role", is(ADMIN)));
    }

    @Test
    void checkUpdateShouldThrowValidationExc() throws Exception {
        UserUpdateDto user = new UserUpdateDto();
        user.setId(1L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword("short");
        user.setRole(UserRole.ADMIN);

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkUpdateShouldReturnBadRequest() throws Exception {
        UserUpdateDto user = new UserUpdateDto();
        user.setId(2L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword("short");
        user.setRole(UserRole.ADMIN);

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete() throws Exception {
        Mockito.doNothing().when(service).deleteById(1L);
        mvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void checkFindSecureUserShouldReturnEqualsPasswordAndStatus200() throws Exception {
        UserSecureDto user = new UserSecureDto();
        user.setId(1L);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setRole(UserRole.ADMIN);
        Mockito.doReturn(user).when(service).findSecureUser(EMAIL);
        mvc.perform(post(BASE_URL + "/secure")
                        .param("email", EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.password", is(PASSWORD)));
    }
}