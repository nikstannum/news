package ru.clevertec.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private static final String BASE_URL = "/v1/comments";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService service;

    @Test
    void checkCreateShouldSuccessAndStatus201() throws Exception {
        CommentReadDto commentReadDto = new CommentReadDto();
        commentReadDto.setId(1L);
        commentReadDto.setUserId(1L);
        commentReadDto.setText(TEXT);

        doReturn(commentReadDto).when(service).create(any());

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setUserId(1L);
        commentCreateDto.setNewsId(1L);
        commentCreateDto.setText(TEXT);

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(TEXT)));
    }

    @Test
    void checkCreateShouldReturnStatus422() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setUserId(1L);
        commentCreateDto.setNewsId(1L);

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkFindAllShouldHasSize2AndStatus200() throws Exception {
        CommentReadDto comment1 = prepareCommentReadDto();
        CommentReadDto comment2 = prepareCommentReadDto();
        List<CommentReadDto> list = List.of(comment1, comment2);

        doReturn(list).when(service).findAll(1, 2);

        mvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private CommentReadDto prepareCommentReadDto() {
        CommentReadDto dto = new CommentReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setText(TEXT);
        return dto;
    }

    @Test
    void findById() throws Exception {
        CommentReadDto comment = prepareCommentReadDto();
        doReturn(comment).when(service).findById(1L);
        mvc.perform(get(BASE_URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void checkFindByParamsShouldHasSize2AndStatus200() throws Exception {
        CommentReadDto comment1 = prepareCommentReadDto();
        CommentReadDto comment2 = prepareCommentReadDto();
        List<CommentReadDto> list = List.of(comment1, comment2);
        QueryCommentParams params = new QueryCommentParams();
        params.setUser_id(1L);
        params.setText(TEXT);

        doReturn(list).when(service).findByParams(any(), any(), any());

        mvc.perform(get(BASE_URL + "/params")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void checkUpdateShouldEqualsTextAndStatus200() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1L);
        commentUpdateDto.setText(TEXT);
        commentUpdateDto.setUserId(1L);
        commentUpdateDto.setNewsId(1L);
        CommentReadDto commentReadDto = prepareCommentReadDto();

        Mockito.doReturn(commentReadDto).when(service).update(Mockito.any());

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.text", is(TEXT)));
    }

    @Test
    void checkUpdateShouldReturnStatus400() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(2L);
        commentUpdateDto.setText(TEXT);
        commentUpdateDto.setUserId(1L);
        commentUpdateDto.setNewsId(1L);

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkUpdateShouldReturnStatus422() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1L);
        commentUpdateDto.setText(TEXT);
        commentUpdateDto.setUserId(1L);


        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkDeleteShouldReturnStatus204() throws Exception {
        Mockito.doNothing().when(service).deleteById(1L);
        mvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}