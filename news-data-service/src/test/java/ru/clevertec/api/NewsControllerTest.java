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
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.NewsUpdateDto;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    private static final String BASE_URL = "/v1/news";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private NewsService service;

    @Test
    void checkCreateShouldReturnNewsAndStatus201() throws Exception {
        NewsReadDto newsReadDto = new NewsReadDto();
        newsReadDto.setId(1L);
        newsReadDto.setTitle(TITLE);
        newsReadDto.setText(TEXT);
        newsReadDto.setUserId(1L);

        doReturn(newsReadDto).when(service).create(any());

        NewsCreateDto dto = new NewsCreateDto();
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(TITLE)))
                .andExpect(jsonPath("$.text", is(TEXT)));
    }

    @Test
    void checkCreateShouldReturnStatus422() throws Exception {
        NewsCreateDto dto = new NewsCreateDto();
        dto.setUserId(1L);
        dto.setText(TEXT);

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkFindAllShouldReturnSize2AndStatus200() throws Exception {
        NewsReadDto news1 = prepareNewsReadDto();
        NewsReadDto news2 = prepareNewsReadDto();
        List<NewsReadDto> list = List.of(news1, news2);

        doReturn(list).when(service).findAll(1, 2);

        mvc.perform(get(BASE_URL)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private NewsReadDto prepareNewsReadDto() {
        NewsReadDto news = new NewsReadDto();
        news.setId(1L);
        news.setUserId(1L);
        news.setTitle(TITLE);
        news.setText(TEXT);
        CommentReadDto comment1 = new CommentReadDto();
        CommentReadDto comment2 = new CommentReadDto();
        List<CommentReadDto> commentReadDtoList = List.of(comment1, comment2);
        news.setComments(commentReadDtoList);
        return news;
    }

    @Test
    void checkFindByIdShouldReturnNewsAndStatus200() throws Exception {
        NewsReadDto news = prepareNewsReadDto();

        doReturn(news).when(service).findById(1L, 1, 2);

        mvc.perform(get(BASE_URL + "/{id}", 1L)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comments", hasSize(2)));
    }

    @Test
    void checkFindByParamsShouldReturnSize2AndStatus200() throws Exception {
        NewsReadDto news1 = prepareNewsReadDto();
        NewsReadDto news2 = prepareNewsReadDto();
        List<NewsReadDto> list = List.of(news1, news2);
        NewsQueryParams params = new NewsQueryParams();
        params.setUser_id(1L);
        params.setText(TEXT);
        params.setTitle(TITLE);

        doReturn(list).when(service).findByParams(any(), any(), any(), any());

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
    void checkUpdateShouldReturnTextEqualsAndStatus200() throws Exception {
        NewsUpdateDto newsUpdateDto = new NewsUpdateDto();
        newsUpdateDto.setId(1L);
        newsUpdateDto.setText(TEXT);
        newsUpdateDto.setTitle(TITLE);
        newsUpdateDto.setUserId(1L);
        NewsReadDto newsReadDto = prepareNewsReadDto();

        Mockito.doReturn(newsReadDto).when(service).update(Mockito.any());

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.text", is(TEXT)));
    }

    @Test
    void checkUpdateShouldReturnStatus400() throws Exception {
        NewsUpdateDto newsUpdateDto = new NewsUpdateDto();
        newsUpdateDto.setId(2L);
        newsUpdateDto.setText(TEXT);
        newsUpdateDto.setTitle(TITLE);
        newsUpdateDto.setUserId(1L);

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkUpdateShouldReturnStatus422() throws Exception {
        NewsUpdateDto newsUpdateDto = new NewsUpdateDto();
        newsUpdateDto.setId(1L);
        newsUpdateDto.setText(TEXT);
        newsUpdateDto.setTitle(TITLE);

        mvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsUpdateDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkDeleteShouldReturnStatus204() throws Exception {
        Mockito.doNothing().when(service).deleteById(1L);
        mvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}