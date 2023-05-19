package ru.clevertec.service.impl;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.BaseIntegrationTest;
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentServiceImplIntegrationTest extends BaseIntegrationTest {
    public static final String SOME_TEXT = "some text";
    @Autowired
    CommentService service;
    @Autowired
    private EntityManager manager;

    @Test
    void checkCreateShouldReturnIdNotNull() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setNewsId(1L);
        dto.setUserId(1L);
        dto.setText(SOME_TEXT);
        CommentReadDto actual = service.create(dto);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void checkCreateShouldThrowNotFoundExc() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setNewsId(1000L);
        dto.setUserId(1L);
        dto.setText(SOME_TEXT);
        Assertions.assertThrows(NotFoundException.class, () -> service.create(dto));
    }

    @Test
    void checkFindAllShouldHasSize2() {
        List<CommentReadDto> actual = service.findAll(1, 2);
        int expectedSize = 2;
        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void findByIdShouldReturnNotNull() {
        CommentReadDto actual = service.findById(1L);
        assertThat(actual).isNotNull();
    }

    @Test
    void findByIdShouldThrowNotFoundException() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(1000L));
    }

    @Test
    void checkFindByParamsShouldHasSize2() {
        QueryCommentParams params = new QueryCommentParams();
        params.setNews_id(1L);
        int expSize = 2;
        List<CommentReadDto> actual = service.findByParams(1, 2, params);
        assertThat(actual).hasSize(expSize);
    }

    @Test
    void checkUpdateShouldReturnTextEquals() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1L);
        commentUpdateDto.setNewsId(1L);
        commentUpdateDto.setText(SOME_TEXT);
        CommentReadDto actual = service.update(commentUpdateDto);
        assertThat(actual.getText()).isEqualTo(SOME_TEXT);
    }

    @Test
    void checkUpdateShouldThrowBadRequestExc() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1L);
        commentUpdateDto.setNewsId(2L);
        Assertions.assertThrows(BadRequestException.class, () -> service.update(commentUpdateDto));
    }


    @Test
    void checkUpdateShouldThrowNotFoundExc() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1L);
        commentUpdateDto.setNewsId(10000L);
        Assertions.assertThrows(NotFoundException.class, () -> service.update(commentUpdateDto));
    }

    @Test
    void checkDeleteShouldSuccess() {
        service.deleteById(5L);
        manager.flush();
    }
}
