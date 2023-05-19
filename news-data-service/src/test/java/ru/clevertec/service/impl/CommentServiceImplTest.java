package ru.clevertec.service.impl;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.data.CommentRepository;
import ru.clevertec.data.NewsRepository;
import ru.clevertec.data.entity.Comment;
import ru.clevertec.data.util.CommentSpecificationBuilder;
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;
import ru.clevertec.service.mapper.CommentMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    private static final String SOME_COMMENT_TEXT = "some text";
    private static final String ATTRIBUTE_ID = "id";
    @Captor
    ArgumentCaptor<Long> captor;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NewsRepository newsRepository;
    @Mock
    private CommentMapper mapper;
    @Mock
    private CommentSpecificationBuilder specificationBuilder;
    @InjectMocks
    private CommentServiceImpl service;

    @Test
    void checkCreateShouldReturnEquals() {
        doReturn(true).when(newsRepository).existsById(1L);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setNewsId(1L);
        commentCreateDto.setUserId(1L);
        commentCreateDto.setText(SOME_COMMENT_TEXT);
        Comment comment = getStandardComment(null);
        doReturn(comment).when(mapper).toComment(commentCreateDto);
        Comment commentAfterSaving = getStandardComment(1L);
        doReturn(commentAfterSaving).when(commentRepository).save(comment);
        CommentReadDto expected = getStandardCommentReadDto(1L);
        doReturn(expected).when(mapper).toCommentReadDto(commentAfterSaving);

        CommentReadDto actual = service.create(commentCreateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkCreateShouldThrowNotFoundExc() {
        doReturn(false).when(newsRepository).existsById(any());
        CommentCreateDto dto = new CommentCreateDto();
        dto.setNewsId(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.create(dto));
    }

    private CommentReadDto getStandardCommentReadDto(Long id) {
        CommentReadDto dto = new CommentReadDto();
        dto.setId(id);
        dto.setUserId(1L);
        dto.setText(SOME_COMMENT_TEXT);
        return dto;
    }

    private Comment getStandardComment(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUserId(1L);
        comment.setNewsId(1L);
        comment.setText(SOME_COMMENT_TEXT);
        return comment;
    }

    @Test
    void checkFindAllShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, ATTRIBUTE_ID);
        Comment comment1 = getStandardComment(1L);
        Comment comment2 = getStandardComment(2L);
        List<Comment> commentList = List.of(comment1, comment2);
        Page<Comment> page = new PageImpl<>(commentList);
        doReturn(page).when(commentRepository).findAll(pageable);
        doReturn(new CommentReadDto()).when(mapper).toCommentReadDto(comment1);
        doReturn(new CommentReadDto()).when(mapper).toCommentReadDto(comment2);
        int expectedSize = 2;

        List<CommentReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        Comment comment = getStandardComment(1L);
        doReturn(Optional.of(comment)).when(commentRepository).findById(1L);
        CommentReadDto expected = getStandardCommentReadDto(1L);
        doReturn(expected).when(mapper).toCommentReadDto(comment);

        CommentReadDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        doReturn(Optional.empty()).when(commentRepository).findById(any());
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void checkFindByParamsShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, ATTRIBUTE_ID);
        Comment comment1 = getStandardComment(1L);
        Comment comment2 = getStandardComment(2L);
        List<Comment> commentList = List.of(comment1, comment2);
        Page<Comment> page = new PageImpl<>(commentList);
        Specification<Comment> specification = (root, query, criteriaBuilder) -> null;
        doReturn(specification).when(specificationBuilder).getSpecificationSelectCommentByParams(any());
        doReturn(page).when(commentRepository).findAll(specification, pageable);
        doReturn(new CommentReadDto()).when(mapper).toCommentReadDto(any());
        int expectedSize = 2;

        List<CommentReadDto> actual = service.findByParams(1, 2, new QueryCommentParams());

        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        doReturn(true).when(newsRepository).existsById(1L);
        Comment comment = getStandardComment(1L);
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setId(1L);
        dto.setNewsId(1L);
        dto.setText(SOME_COMMENT_TEXT);
        doReturn(Optional.of(comment)).when(commentRepository).findById(1L);
        doReturn(comment).when(commentRepository).save(comment);
        CommentReadDto expected = getStandardCommentReadDto(1L);
        doReturn(expected).when(mapper).toCommentReadDto(comment);

        CommentReadDto actual = service.update(dto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkUpdateShouldThrowBadRequestExc() {
        doReturn(true).when(newsRepository).existsById(1L);
        Comment comment = getStandardComment(1L);
        comment.setNewsId(2L);
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setId(1L);
        dto.setNewsId(1L);
        dto.setText(SOME_COMMENT_TEXT);
        doReturn(Optional.of(comment)).when(commentRepository).findById(1L);
        Assertions.assertThrows(BadRequestException.class, () -> service.update(dto));
    }

    @Test
    void checkUpdateShouldThrowNotFoundExc() {
        doReturn(false).when(newsRepository).existsById(any());
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setNewsId(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.update(dto));
    }

    @Test
    void checkDeleteByIdShouldCapture() {
        service.deleteById(1L);
        Mockito.verify(commentRepository).deleteById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);
    }
}