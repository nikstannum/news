package ru.clevertec.service.impl;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.BaseIntegrationTest;
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.NewsUpdateDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

import static org.assertj.core.api.Assertions.assertThat;

public class NewsServiceImplIntegrationTest extends BaseIntegrationTest {
    private static final String SOME_TITLE = "some title";
    private static final String SOME_TEXT = "some text";
    @Autowired
    private NewsService service;
    @Autowired
    private EntityManager manager;

    @Test
    void checkCreateShouldReturnIdNotNull() {
        NewsCreateDto dto = new NewsCreateDto();
        dto.setUserId(1L);
        dto.setText(SOME_TEXT);
        dto.setTitle(SOME_TITLE);
        NewsReadDto created = service.create(dto);
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void checkFindAllShouldHasSize2() {
        List<SimpleNewsReadDto> actual = service.findAll(1, 2);
        assertThat(actual).hasSize(2);
    }

    @Test
    void findByIdShouldReturnNotNull() {
        NewsReadDto actual = service.findById(1L, 1, 2);
        assertThat(actual).isNotNull();
    }

    @Test
    void findByIdShouldThrowNotFoundExc() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(200L, 1, 2));
    }

    @Test
    void findByParamsWithKeyWordShouldListIsNotEmpty() {
        NewsQueryParams params = new NewsQueryParams();
        params.setUser_id(2L);
        List<SimpleNewsReadDto> actual = service.findByParams(1, 2, "news", params);
        assertThat(actual).isNotEmpty();
    }

    @Test
    void findByParamsWithoutKeyWordShouldListIsNotEmpty() {
        NewsQueryParams params = new NewsQueryParams();
        params.setUser_id(2L);
        List<SimpleNewsReadDto> actual = service.findByParams(1, 2, null, params);
        assertThat(actual).isNotEmpty();
    }

    @Test
    void checkUpdateShouldReturnTextEquals() {
        NewsUpdateDto dto = new NewsUpdateDto();
        dto.setId(1L);
        dto.setUserId(2L);
        dto.setTitle(SOME_TITLE);
        dto.setText(SOME_TEXT);
        NewsReadDto actual = service.update(dto);
        assertThat(actual.getText()).isEqualTo(SOME_TEXT);
    }

    @Test
    void checkUpdateShouldThrowNotFoundExc() {
        NewsUpdateDto dto = new NewsUpdateDto();
        dto.setId(500L);
        Assertions.assertThrows(NotFoundException.class, () -> service.update(dto));
    }

    @Test
    void checkDeleteShouldSuccess() {
        service.deleteById(5L);
        manager.flush();
    }
}
