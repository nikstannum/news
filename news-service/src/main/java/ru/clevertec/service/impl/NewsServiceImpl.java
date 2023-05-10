package ru.clevertec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.clevertec.client.NewsDataServiceClient;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.NewsUpdateDto;
import ru.clevertec.client.dto.SimpleNewsReadDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.News;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.CommentMapper;
import ru.clevertec.service.mapper.NewsMapper;
import ru.clevertec.util.cache.CacheDelete;
import ru.clevertec.util.cache.CacheGet;
import ru.clevertec.util.cache.CachePutPost;
import ru.clevertec.util.logger.LogInvocation;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    public static final String EXC_MSG_SOMEONE_ELSE_NEWS = "You can only delete your news";
    private final NewsDataServiceClient newsClient;
    private final UserDataServiceClient userClient;
    private final NewsMapper newsMapper;
    private final CommentMapper commentMapper;
    private final AuthorMapper authorMapper;

    @Override
    public List<ClientSimpleNewsReadDto> findAll(Integer page, Integer size) {
        List<SimpleNewsReadDto> simpleList = newsClient.getAll(page, size);
        return collectListClientSimpleNewsReadDto(simpleList);
    }

    private List<ClientSimpleNewsReadDto> collectListClientSimpleNewsReadDto(List<SimpleNewsReadDto> simpleList) {
        List<Long> userIds = simpleList.stream()
                .map(SimpleNewsReadDto::getUserId)
                .toList();
        List<UserDto> userList = userClient.getAllUsersByIds(userIds);
        Map<Long, UserDto> userMap = new HashMap<>();
        for (UserDto user : userList) {
            userMap.put(user.getId(), user);
        }
        List<ClientSimpleNewsReadDto> clientList = new ArrayList<>();
        List<News> newsList = simpleList.stream()
                .map(newsMapper::toNew)
                .toList();
        for (News news : newsList) {
            UserDto user = userMap.get(news.getUserId());
            if (user != null) {
                AuthorReadDto author = authorMapper.toDto(user);
                ClientSimpleNewsReadDto clientSimpleNewsReadDto = newsMapper.toClientNewsReadDto(news);
                clientSimpleNewsReadDto.setAuthor(author);
                clientList.add(clientSimpleNewsReadDto);
            }
        }
        return clientList;
    }

    @Override
    @CacheGet
    @Cacheable(value = "news", key = "#id")
    @LogInvocation
    public ClientNewsReadDto findById(Long id, Integer page, Integer size) {
        NewsReadDto newsReadDto = newsClient.getById(id, page, size);
        List<CommentReadDto> commentReadDtoList = newsReadDto.getComments();
        List<Long> commentAndNewsAuthorsIds = new ArrayList<>(commentReadDtoList.stream()
                .map(CommentReadDto::getUserId)
                .toList());
        commentAndNewsAuthorsIds.add(newsReadDto.getUserId());
        List<UserDto> userDtoList = userClient.getAllUsersByIds(commentAndNewsAuthorsIds);
        Map<Long, UserDto> mapNewsCommentsAuthor = new HashMap<>();
        for (UserDto userDto : userDtoList) {
            mapNewsCommentsAuthor.put(userDto.getId(), userDto);
        }
        UserDto newsAuthorUserDto = mapNewsCommentsAuthor.remove(newsReadDto.getUserId());
        AuthorReadDto newsAuthor = authorMapper.toDto(newsAuthorUserDto);
        ClientNewsReadDto news = newsMapper.toClientNewsReadDto(newsReadDto);
        news.setAuthor(newsAuthor);

        List<ClientCommentReadDto> comments = getClientCommentReadDtoList(commentReadDtoList, mapNewsCommentsAuthor);
        news.setComments(comments);
        return news;
    }

    private List<ClientCommentReadDto> getClientCommentReadDtoList(List<CommentReadDto> commentReadDtoList, Map<Long, UserDto> mapNewsCommentsAuthor) {
        List<ClientCommentReadDto> comments = new ArrayList<>();
        for (CommentReadDto commentReadDto : commentReadDtoList) {
            Long authorCommentId = commentReadDto.getUserId();
            ClientCommentReadDto comment = commentMapper.toClientCommentReadDto(commentReadDto);
            UserDto commentAuthorUserDto = mapNewsCommentsAuthor.get(authorCommentId);
            AuthorReadDto author = authorMapper.toDto(commentAuthorUserDto);
            comment.setAuthor(author);
            comments.add(comment);
        }
        return comments;
    }

    @Override
    public List<ClientSimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, QueryParamsNews params) {
        List<SimpleNewsReadDto> simpleList = newsClient.getByParams(page, size, keyWord, params);
        return collectListClientSimpleNewsReadDto(simpleList);
    }

    @Override
    @CachePutPost
    @CachePut(value = "news", key = "#result.id")
    @LogInvocation
    public ClientNewsReadDto create(ClientNewsCreateDto clientNewsCreateDto) {
        String email = clientNewsCreateDto.getEmail();
        UserDto userDto = userClient.getByEmail(email);
        Long userId = userDto.getId();
        NewsCreateDto newsCreateDto = newsMapper.toNewsCreateDto(clientNewsCreateDto);
        newsCreateDto.setUserId(userId);
        ResponseEntity<NewsReadDto> newsReadDtoResponseEntity = newsClient.create(newsCreateDto);
        NewsReadDto createdNewsReadDto = newsReadDtoResponseEntity.getBody();
        ClientNewsReadDto clientNewsReadDto = newsMapper.toClientNewsReadDto(createdNewsReadDto);
        AuthorReadDto author = authorMapper.toDto(userDto);
        clientNewsReadDto.setAuthor(author);
        return clientNewsReadDto;
    }

    @Override
    @CachePutPost
    @CachePut(value = "news", key = "#clientNewsUpdateDto.id")
    @LogInvocation
    public ClientNewsReadDto update(Long id, ClientNewsUpdateDto clientNewsUpdateDto) {
        String email = clientNewsUpdateDto.getEmail();
        UserDto userDto = userClient.getByEmail(email);
        Long userId = userDto.getId();
        NewsUpdateDto newsUpdateDto = newsMapper.toNewsUpdateDto(clientNewsUpdateDto);
        newsUpdateDto.setUserId(userId);
        NewsReadDto newsReadDto = newsClient.update(id, newsUpdateDto);
        ClientNewsReadDto clientNewsReadDto = newsMapper.toClientNewsReadDto(newsReadDto);
        AuthorReadDto author = authorMapper.toDto(userDto);
        clientNewsReadDto.setAuthor(author);
        return clientNewsReadDto;
    }

    @Override
    @CacheDelete
    @CacheEvict(value = "news", key = "#id")
    @LogInvocation
    public void delete(Long id) {
        NewsReadDto newsReadDto = newsClient.getById(id, 1, 1);
        Long authorId = newsReadDto.getUserId();
        Long authenticationId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (!authorId.equals(authenticationId)) {
            throw new AuthenticationException(EXC_MSG_SOMEONE_ELSE_NEWS);
        }
        newsClient.deleteById(id);
    }
}
