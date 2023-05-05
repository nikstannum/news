package ru.clevertec.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clevertec.client.comment.Comment;
import ru.clevertec.client.comment.CommentDataServiceClient;
import ru.clevertec.client.news.News;
import ru.clevertec.client.news.NewsDataServiceClient;
import ru.clevertec.client.user.User;
import ru.clevertec.client.user.UserDataServiceClient;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.NewsCreateUpdateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.QueryParamsNews;
import ru.clevertec.service.dto.SimpleNewsReadDto;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.NewsMapper;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsDataServiceClient newsClient;
    private final CommentDataServiceClient commentClient;
    private final UserDataServiceClient userClient;
    private final NewsMapper newsMapper;
    private final AuthorMapper authorMapper;

    @Override
    public List<SimpleNewsReadDto> findAll(Integer page, Integer size) {
        List<News> newsList = newsClient.getAll(page, size);
        return newsList.stream().map(newsMapper::toSimpleNewsReadDto).toList();
    }

    @Override
    public NewsReadDto findById(Long id, Integer page, Integer size) {
        News news = newsClient.getById(id);
        NewsReadDto newsReadDto = newsMapper.toDto(news);
        QueryParamsComment queryParams = new QueryParamsComment();
        queryParams.setNews_id(id);
        List<Comment> commentList = commentClient.getByParams(page, size, queryParams);
        newsReadDto.setComments(commentList);
        Long userId = news.getUserId();
        User user = userClient.getById(userId);
        AuthorReadDto author = authorMapper.toDto(user);
        newsReadDto.setAuthor(author);
        return newsReadDto;
    }

    @Override
    public List<SimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, QueryParamsNews params) {
        List<News> newsList = newsClient.getByParams(page, size, keyWord, params);
        return newsList.stream().map(newsMapper::toSimpleNewsReadDto).toList();
    }

    @Override
    public NewsReadDto create(NewsCreateUpdateDto newsCreateDto) {
        News news = newsMapper.toNews(newsCreateDto);
        ResponseEntity<News> createdEntity = newsClient.create(news);
        News created = createdEntity.getBody();
        NewsReadDto createdDto = newsMapper.toDto(created);
        User user = userClient.getById(newsCreateDto.getUserId());
        AuthorReadDto author = authorMapper.toDto(user);
        createdDto.setAuthor(author);
        return createdDto;
    }

    @Override
    public SimpleNewsReadDto update(Long id, NewsCreateUpdateDto newsCreateDto) {
        News news = newsMapper.toNews(newsCreateDto);
        News updated = newsClient.update(id, news);
        return newsMapper.toSimpleNewsReadDto(updated);
    }

    @Override
    public void delete(Long id) {
        newsClient.deleteById(id);
    }
}
