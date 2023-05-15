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
import ru.clevertec.data.entity.News;
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.data.util.NewsSpecificationBuilder;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.NewsUpdateDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;
import ru.clevertec.service.mapper.NewsMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String ATTRIBUTE_ID = "id";
    @Captor
    ArgumentCaptor<Long> captor;
    @Mock
    private NewsMapper mapper;
    @Mock
    private NewsRepository newsRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NewsSpecificationBuilder specificationBuilder;
    @InjectMocks
    private NewsServiceImpl service;

    @Test
    void checkCreateShouldReturnEquals() {
        NewsCreateDto newsCreateDto = getStandardNewsCreateDto();
        News news = new News();
        news.setUserId(newsCreateDto.getUserId());
        news.setTitle(newsCreateDto.getTitle());
        news.setText(newsCreateDto.getText());
        doReturn(news).when(mapper).toNews(newsCreateDto);
        News created = getStandardNews(1L);
        doReturn(created).when(newsRepository).save(news);
        NewsReadDto expected = getStandardNewsReadDtoWithoutComments(1L);
        doReturn(expected).when(mapper).toNewsReadDto(created);

        NewsReadDto actual = service.create(newsCreateDto);

        assertThat(actual).isEqualTo(expected);
    }

    private NewsReadDto getStandardNewsReadDtoWithoutComments(Long id) {
        NewsReadDto news = new NewsReadDto();
        news.setId(id);
        news.setUserId(USER_ID);
        news.setTitle(TITLE);
        news.setText(TEXT);
        return news;
    }

    private News getStandardNews(Long id) {
        News news = new News();
        news.setId(id);
        news.setUserId(USER_ID);
        news.setTitle(TITLE);
        news.setText(TEXT);
        return news;
    }

    private NewsCreateDto getStandardNewsCreateDto() {
        NewsCreateDto news = new NewsCreateDto();
        news.setUserId(USER_ID);
        news.setTitle(TITLE);
        news.setText(TEXT);
        return news;
    }

    @Test
    void checkFindAllShouldHasSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, ATTRIBUTE_ID);
        News news1 = getStandardNews(1L);
        News news2 = getStandardNews(2L);
        List<News> newsList = List.of(news1, news2);
        Page<News> page = new PageImpl<>(newsList);
        doReturn(page).when(newsRepository).findAll(pageable);
        doReturn(new SimpleNewsReadDto()).when(mapper).toSimpleNewsReadDto(news1);
        doReturn(new SimpleNewsReadDto()).when(mapper).toSimpleNewsReadDto(news2);
        int expectedSize = 2;

        List<SimpleNewsReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        News news = getStandardNews(1L);
        doReturn(Optional.of(news)).when(newsRepository).findById(1L);
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, ATTRIBUTE_ID);
        Comment comment1 = getStandardComment(1L);
        Comment comment2 = getStandardComment(2L);
        List<Comment> comments = List.of(comment1, comment2);
        doReturn(comments).when(commentRepository).findByNewsId(1L, pageable);
        news.setComments(comments);
        NewsReadDto newsReadDto = getStandardNewsReadDtoWithoutComments(1L);
        CommentReadDto commentReadDto1 = new CommentReadDto();
        CommentReadDto commentReadDto2 = new CommentReadDto();
        newsReadDto.setComments(List.of(commentReadDto1, commentReadDto2));
        doReturn(newsReadDto).when(mapper).toNewsReadDto(news);

        NewsReadDto actual = service.findById(1L, 1, 2);
        assertThat(actual.getComments()).hasSize(2);
    }

    private Comment getStandardComment(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUserId(USER_ID);
        comment.setText(TEXT);
        return comment;
    }

    @Test
    void checkFindByIdShouldThrowNotFoundExc() {
        doReturn(Optional.empty()).when(newsRepository).findById(1L);
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(1L, 1, 2));
    }

    @Test
    void checkFindByParamsWithKeyWordShouldReturnSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, ATTRIBUTE_ID);
        String keyWord = "keyword";
        News news1 = getStandardNews(1L);
        News news2 = getStandardNews(2L);
        List<News> list = List.of(news1, news2);
        Page<News> page = new PageImpl<>(list);
        doReturn(page).when(newsRepository).findByTitleContainsOrTextContains(keyWord, keyWord, pageable);
        SimpleNewsReadDto dto = new SimpleNewsReadDto();
        doReturn(dto).when(mapper).toSimpleNewsReadDto(news1);
        doReturn(dto).when(mapper).toSimpleNewsReadDto(news2);
        int expectedSize = 2;

        List<SimpleNewsReadDto> actual = service.findByParams(1, 2, keyWord, new NewsQueryParams());

        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkFindByParamsWithoutKeyWordShouldReturnSize2() {
        Pageable pageable = PageRequest.of(0, 2, Direction.ASC, ATTRIBUTE_ID);
        News news1 = getStandardNews(1L);
        News news2 = getStandardNews(2L);
        List<News> list = List.of(news1, news2);
        Page<News> page = new PageImpl<>(list);
        Specification<News> specification = (root, query, criteriaBuilder) -> null;
        doReturn(specification).when(specificationBuilder).getSpecificationSelectNewsByParams(any());
        doReturn(page).when(newsRepository).findAll(specification, pageable);
        SimpleNewsReadDto dto = new SimpleNewsReadDto();
        doReturn(dto).when(mapper).toSimpleNewsReadDto(news1);
        doReturn(dto).when(mapper).toSimpleNewsReadDto(news2);
        int expectedSize = 2;

        List<SimpleNewsReadDto> actual = service.findByParams(1, 2, null, new NewsQueryParams());

        assertThat(actual).hasSize(expectedSize);
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        News news = getStandardNews(1L);
        doReturn(Optional.of(news)).when(newsRepository).findById(1L);
        news.setText("new text");
        News newsUpd = getStandardNews(1L);
        newsUpd.setText("new text");
        doReturn(newsUpd).when(newsRepository).save(news);

        Pageable pageable = PageRequest.of(0, 10, Direction.ASC, ATTRIBUTE_ID);
        Comment comment1 = getStandardComment(1L);
        Comment comment2 = getStandardComment(2L);
        List<Comment> list = List.of(comment1, comment2);
        doReturn(list).when(commentRepository).findByNewsId(1L, pageable);

        NewsReadDto newsReadDto = getStandardNewsReadDtoWithoutComments(1L);
        CommentReadDto commentReadDto1 = new CommentReadDto();
        commentReadDto1.setId(1L);
        CommentReadDto commentReadDto2 = new CommentReadDto();
        commentReadDto2.setId(2L);
        List<CommentReadDto> commentReadDtoList = List.of(commentReadDto1, commentReadDto2);
        newsReadDto.setComments(commentReadDtoList);
        doReturn(newsReadDto).when(mapper).toNewsReadDto(news);
        NewsUpdateDto dto = new NewsUpdateDto();
        dto.setId(1L);
        dto.setText("new text");

        NewsReadDto actual = service.update(dto);

        assertThat(actual).isEqualTo(newsReadDto);
    }

    @Test
    void checkUpdateShouldThrowNotFoundExc() {
        doReturn(Optional.empty()).when(newsRepository).findById(1L);
        NewsUpdateDto dto = new NewsUpdateDto();
        dto.setId(1L);

        Assertions.assertThrows(NotFoundException.class, () -> service.update(dto));
    }

    @Test
    void checkDeleteByIdShouldCapture() {
        service.deleteById(1L);
        Mockito.verify(newsRepository).deleteById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);
    }
}