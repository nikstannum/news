package ru.clevertec.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
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
import ru.clevertec.client.entity.User.UserRole;
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
    @LogInvocation
    public List<ClientSimpleNewsReadDto> findAll(Integer page, Integer size) {
        List<SimpleNewsReadDto> simpleList = newsClient.getAll(page, size);
        return collectListClientSimpleNewsReadDto(simpleList);
    }

    /**
     * A list of comments is being compiled. The main logic is built around getting a list of news authors by their IDs and adding news authors
     * ({@link ru.clevertec.service.dto.AuthorReadDto}) to the corresponding news.
     *
     * @param simpleList list obtained from non-public microservice news-data-service
     * @return list of news
     */
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
                AuthorReadDto author = authorMapper.toAuthor(user);
                ClientSimpleNewsReadDto clientSimpleNewsReadDto = newsMapper.toClientNewsReadDto(news);
                clientSimpleNewsReadDto.setAuthor(author);
                clientList.add(clientSimpleNewsReadDto);
            }
        }
        return clientList;
    }

    @Override
    @CacheGet
    @Cacheable(value = "news", key = "#id + ' '+  #page + ' ' +  #size")
    @LogInvocation
    public ClientNewsReadDto findById(Long id, Integer page, Integer size) {
        NewsReadDto newsReadDto = newsClient.getById(id, page, size);
        return getClientNewsReadDto(newsReadDto);
    }

    /**
     * collects full-fledged news to provide the user
     *
     * @param newsReadDto unprocessed news received from a non-public microservice news-data-service
     * @return processed news
     */
    private ClientNewsReadDto getClientNewsReadDto(NewsReadDto newsReadDto) {
        List<CommentReadDto> commentReadDtoList = newsReadDto.getComments();
        List<UserDto> userDtoList = getCommentsAndNewsAuthorsList(newsReadDto, commentReadDtoList);
        Map<Long, UserDto> mapNewsCommentsAuthors = new HashMap<>();
        for (UserDto userDto : userDtoList) {
            mapNewsCommentsAuthors.put(userDto.getId(), userDto);
        }
        UserDto newsAuthorUserDto = mapNewsCommentsAuthors.remove(newsReadDto.getUserId());
        AuthorReadDto newsAuthor = authorMapper.toAuthor(newsAuthorUserDto);
        ClientNewsReadDto news = newsMapper.toClientNewsReadDto(newsReadDto);
        news.setAuthor(newsAuthor);
        List<ClientCommentReadDto> comments = getClientCommentReadDtoList(commentReadDtoList, mapNewsCommentsAuthors);
        news.setComments(comments);
        return news;
    }

    /**
     * getting the author of the news and the authors of the comments
     *
     * @param newsReadDto        unprocessed news received from a non-public microservice news-data-service
     * @param commentReadDtoList list of raw comments received together with the news from the non-public news-data-service
     * @return raw list of news author and news comment authors
     */
    private List<UserDto> getCommentsAndNewsAuthorsList(NewsReadDto newsReadDto, List<CommentReadDto> commentReadDtoList) {
        List<Long> commentAndNewsAuthorsIds = new ArrayList<>(commentReadDtoList.stream()
                .map(CommentReadDto::getUserId)
                .toList());
        commentAndNewsAuthorsIds.add(newsReadDto.getUserId());
        return userClient.getAllUsersByIds(commentAndNewsAuthorsIds);
    }

    /**
     * getting the processed list of comments to the news
     *
     * @param commentReadDtoList    list of raw comments received together with the news from the non-public news-data-service
     * @param mapNewsCommentsAuthor unprocessed authors of comments collected in a map
     * @return processed list of news comments
     */
    private List<ClientCommentReadDto> getClientCommentReadDtoList(List<CommentReadDto> commentReadDtoList, Map<Long, UserDto> mapNewsCommentsAuthor) {
        List<ClientCommentReadDto> comments = new ArrayList<>();
        for (CommentReadDto commentReadDto : commentReadDtoList) {
            Long authorCommentId = commentReadDto.getUserId();
            ClientCommentReadDto comment = commentMapper.toClientCommentReadDto(commentReadDto);
            UserDto commentAuthorUserDto = mapNewsCommentsAuthor.get(authorCommentId);
            AuthorReadDto author = authorMapper.toAuthor(commentAuthorUserDto);
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
        AuthorReadDto author = authorMapper.toAuthor(userDto);
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
        return getClientNewsReadDto(newsReadDto);
    }

    @Override
    @CacheDelete
    @CacheEvict(value = "news", key = "#id")
    @LogInvocation
    public void delete(Long id) {
        NewsReadDto newsReadDto = newsClient.getById(id, 1, 1);
        Long authorId = newsReadDto.getUserId();
        Long authenticationId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!authorId.equals(authenticationId) && !authorities.contains(UserRole.ADMIN)) {
            throw new AuthenticationException(EXC_MSG_SOMEONE_ELSE_NEWS);
        }
        newsClient.deleteById(id);
    }
}
