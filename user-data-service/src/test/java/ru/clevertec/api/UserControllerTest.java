package ru.clevertec.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.data.User.UserRole;
import ru.clevertec.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService service;


    @Test
    public void findById() throws Exception {
        UserReadDto userReadDto = prepareUserReadDto();
        Mockito.doReturn(userReadDto).when(service).findById(1L);
        mvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("firstName")))
                .andExpect(jsonPath("$.lastName", is("lastName")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    private UserReadDto prepareUserReadDto() {
        UserReadDto user = new UserReadDto();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email");
        user.setRole(UserRole.ADMIN);
        return user;

    }

    @Test
    void create() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findUsersByIds() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findUser() {
    }
}