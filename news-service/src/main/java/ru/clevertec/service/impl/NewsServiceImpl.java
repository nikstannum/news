package ru.clevertec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clevertec.client.NewsDataServiceClient;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.NewsUpdateDto;
import ru.clevertec.client.dto.SimpleNewsReadDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.News;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.NewsMapper;
import ru.clevertec.util.cache.CacheDelete;
import ru.clevertec.util.cache.CacheGet;
import ru.clevertec.util.cache.CachePutPost;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsDataServiceClient newsClient;
    private final UserDataServiceClient userClient;
    private final NewsMapper newsMapper;
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
                .map(newsMapper::toNews)
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
    public ClientNewsReadDto findById(Long id, Integer page, Integer size) {
        NewsReadDto newsReadDto = newsClient.getById(id, page, size);
        UserDto userDto = userClient.getById(newsReadDto.getUserId());
        AuthorReadDto author = authorMapper.toDto(userDto);
        ClientNewsReadDto news = newsMapper.toClientNewsReadDto(newsReadDto);
        news.setAuthor(author);
        return news;
    }

    @Override
    public List<ClientSimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, QueryParamsNews params) {
        List<SimpleNewsReadDto> simpleList = newsClient.getByParams(page, size, keyWord, params);
        return collectListClientSimpleNewsReadDto(simpleList);
    }

    @Override
    @CachePutPost
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
    public void delete(Long id) {
        newsClient.deleteById(id);
    }
}
