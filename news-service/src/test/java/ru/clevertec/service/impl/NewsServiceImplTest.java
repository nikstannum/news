package ru.clevertec.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    private static final String TEXT = "text";
    private static final String TITLE = "title";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email@email.com";
    @Mock
    private NewsDataServiceClient newsClient;
    @Mock
    private UserDataServiceClient userClient;
    @Mock
    private NewsMapper newsMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private AuthorMapper authorMapper;
    @InjectMocks
    private NewsServiceImpl service;

    private void prepareAuthorMock() {
        AuthorReadDto author = getAuthor();
        doReturn(author).when(authorMapper).toAuthor(any());
    }

    private void prepareCommonMockForGetOps() {
        News news = getStandardNews();
        doReturn(news).when(newsMapper).toNew(any());
        ClientSimpleNewsReadDto clientSimpleNewsReadDto = getClientSimpleNewsReadDto();
        doReturn(clientSimpleNewsReadDto).when(newsMapper).toClientNewsReadDto(news);
        UserDto user1 = getStandardUserDto();
        UserDto user2 = getStandardUserDto();
        List<UserDto> userDtoList = List.of(user1, user2);
        doReturn(userDtoList).when(userClient).getAllUsersByIds(Arrays.asList(1L, 1L));
    }

    @Test
    void checkFindAllShouldHasSize2() {
        prepareAuthorMock();
        prepareCommonMockForGetOps();
        SimpleNewsReadDto newsReadDto1 = getSimpleNewsReadDto();
        SimpleNewsReadDto newsReadDto2 = getSimpleNewsReadDto();
        List<SimpleNewsReadDto> list = List.of(newsReadDto1, newsReadDto2);
        doReturn(list).when(newsClient).getAll(1, 2);
        int expectedSize = 2;

        List<ClientSimpleNewsReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(expectedSize);
    }

    private News getStandardNews() {
        News news = new News();
        news.setId(1L);
        news.setUserId(1L);
        news.setTitle(TITLE);
        news.setText(TEXT);
        return news;
    }

    private UserDto getStandardUserDto() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setUserRole(UserRole.SUBSCRIBER);
        return user;
    }

    private ClientSimpleNewsReadDto getClientSimpleNewsReadDto() {
        ClientSimpleNewsReadDto dto = new ClientSimpleNewsReadDto();
        dto.setId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        AuthorReadDto author = getAuthor();
        dto.setAuthor(author);
        return dto;
    }

    private AuthorReadDto getAuthor() {
        AuthorReadDto dto = new AuthorReadDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        return dto;
    }

    private SimpleNewsReadDto getSimpleNewsReadDto() {
        SimpleNewsReadDto dto = new SimpleNewsReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        return dto;
    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        prepareAuthorMock();
        NewsReadDto newsReadDto = getNewsReadDto();
        doReturn(newsReadDto).when(newsClient).getById(1L, 1, 2);
        UserDto userDto = getStandardUserDto();
        List<UserDto> userDtoList = List.of(userDto);
        doReturn(userDtoList).when(userClient).getAllUsersByIds(Arrays.asList(1L, 1L, 1L));
        ClientNewsReadDto clientNewsReadDto = getClientNewsReadDto();
        doReturn(clientNewsReadDto).when(newsMapper).toClientNewsReadDto(newsReadDto);
        ClientNewsReadDto expected = getClientNewsReadDto();
        doReturn(getClientCommentReadDto()).when(commentMapper).toClientCommentReadDto(any());

        ClientNewsReadDto actual = service.findById(1L, 1, 2);

        assertThat(actual).isEqualTo(expected);
    }

    private ClientNewsReadDto getClientNewsReadDto() {
        ClientNewsReadDto dto = new ClientNewsReadDto();
        dto.setId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        AuthorReadDto author = getAuthor();
        dto.setAuthor(author);
        ClientCommentReadDto comment1 = getClientCommentReadDto();
        ClientCommentReadDto comment2 = getClientCommentReadDto();
        dto.setComments(Arrays.asList(comment1, comment2));
        return dto;
    }

    private ClientCommentReadDto getClientCommentReadDto() {
        ClientCommentReadDto dto = new ClientCommentReadDto();
        dto.setId(1L);
        dto.setAuthor(getAuthor());
        dto.setText(TEXT);
        return dto;
    }

    private NewsReadDto getNewsReadDto() {
        NewsReadDto dto = new NewsReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setTitle(TITLE);
        dto.setText(TEXT);
        CommentReadDto comment1 = getCommentReadDto();
        CommentReadDto comment2 = getCommentReadDto();
        List<CommentReadDto> list = List.of(comment1, comment2);
        dto.setComments(list);
        return dto;
    }

    private CommentReadDto getCommentReadDto() {
        CommentReadDto dto = new CommentReadDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setText(TEXT);
        return dto;
    }

    @Test
    void findByParams() {
        prepareAuthorMock();
        prepareCommonMockForGetOps();
        SimpleNewsReadDto newsReadDto1 = getSimpleNewsReadDto();
        SimpleNewsReadDto newsReadDto2 = getSimpleNewsReadDto();
        List<SimpleNewsReadDto> list = List.of(newsReadDto1, newsReadDto2);
        QueryParamsNews params = new QueryParamsNews();
        params.setUser_id(1L);
        params.setTitle(TITLE);
        params.setText(TEXT);
        doReturn(list).when(newsClient).getByParams(1, 2, "keyword", params);
        int expSize = 2;

        List<ClientSimpleNewsReadDto> actual = service.findByParams(1, 2, "keyword", params);

        assertThat(actual).hasSize(expSize);
    }

    @Test
    void create() {
        prepareAuthorMock();
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(userClient).getByEmail(EMAIL);
        NewsCreateDto newsCreateDto = new NewsCreateDto();
        newsCreateDto.setUserId(1L);
        newsCreateDto.setTitle(TITLE);
        newsCreateDto.setText(TEXT);
        doReturn(newsCreateDto).when(newsMapper).toNewsCreateDto(any());
        NewsReadDto newsReadDto = getNewsReadDto();
        ResponseEntity<NewsReadDto> response = ResponseEntity.status(HttpStatus.CREATED).body(newsReadDto);
        doReturn(response).when(newsClient).create(any());
        ClientNewsReadDto clientNewsReadDto = getClientNewsReadDto();
        doReturn(clientNewsReadDto).when(newsMapper).toClientNewsReadDto(newsReadDto);
        ClientNewsCreateDto createDto = new ClientNewsCreateDto();
        createDto.setText(TEXT);
        createDto.setTitle(TITLE);
        createDto.setEmail(EMAIL);
        ClientNewsReadDto expected = getClientNewsReadDto();

        ClientNewsReadDto actual = service.create(createDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void update() {
        prepareAuthorMock();
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(userClient).getByEmail(EMAIL);
        NewsUpdateDto updateDto = new NewsUpdateDto();
        updateDto.setId(1L);
        updateDto.setUserId(1L);
        updateDto.setTitle(TITLE);
        updateDto.setText(TEXT);
        doReturn(updateDto).when(newsMapper).toNewsUpdateDto(any());
        NewsReadDto newsReadDto = getNewsReadDto();
        doReturn(newsReadDto).when(newsClient).update(1L, updateDto);
        ClientNewsReadDto clientNewsReadDto = getClientNewsReadDto();
        doReturn(clientNewsReadDto).when(newsMapper).toClientNewsReadDto(newsReadDto);
        ClientNewsUpdateDto clientNewsUpdateDto = new ClientNewsUpdateDto();
        clientNewsUpdateDto.setId(1L);
        clientNewsUpdateDto.setEmail(EMAIL);
        clientNewsUpdateDto.setTitle(TITLE);
        clientNewsUpdateDto.setText(TEXT);
        ClientNewsReadDto expected = getClientNewsReadDto();

        ClientNewsReadDto actual = service.update(1L, clientNewsUpdateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void checkDeleteShouldCaptured() {
        prepareSecurityContext(UserRole.ADMIN, 1L);
        NewsReadDto newsReadDto = getNewsReadDto();
        doReturn(newsReadDto).when(newsClient).getById(1L, 1, 1);
        service.delete(1L);
        verify(newsClient).deleteById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);

    }

    @Test
    void checkDeleteShouldThrowAuthenticationExc() {
        prepareSecurityContext(UserRole.SUBSCRIBER, 2L);
        NewsReadDto newsReadDto = getNewsReadDto();
        doReturn(newsReadDto).when(newsClient).getById(1L, 1, 1);
        Assertions.assertThrows(AuthenticationException.class, () -> service.delete(1L));
    }

    private void prepareSecurityContext(UserRole role, Long id) {
        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton(role);
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return id;
            }

            @Override
            public Object getPrincipal() {
                return EMAIL;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}