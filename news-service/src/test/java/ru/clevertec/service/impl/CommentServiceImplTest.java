package ru.clevertec.service.impl;

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
import ru.clevertec.client.dto.CommentCreateDto;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.CommentUpdateDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.ClientSimpleCommentReadDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.CommentMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    private static final String EMAIL = "email@email.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String TEXT = "text";
    @Captor
    ArgumentCaptor<Long> captor;
    @Mock
    private UserDataServiceClient userClient;
    @Mock
    private NewsDataServiceClient newsClient;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private AuthorMapper authorMapper;
    @InjectMocks
    private CommentServiceImpl service;

    private UserDto getStandardUserDto() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setUserRole(UserRole.SUBSCRIBER);
        return user;
    }

    @Test
    void checkCreteShouldReturnEquals() {
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(userClient).getByEmail(any());
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setUserId(1L);
        commentCreateDto.setNewsId(1L);
        commentCreateDto.setText(TEXT);
        doReturn(commentCreateDto).when(commentMapper).toCommentCreateDto(any());
        CommentReadDto commentReadDto = getStandardCommentReadDto();
        ResponseEntity<CommentReadDto> response = ResponseEntity.status(HttpStatus.CREATED).body(commentReadDto);
        doReturn(response).when(newsClient).createComment(any());
        ClientCommentReadDto clientCommentReadDto = getStandardClientCommentReadDto();
        doReturn(clientCommentReadDto).when(commentMapper).toClientCommentReadDto(any());
        doReturn(getStandardAuthor()).when(authorMapper).toAuthor(any());
        ClientCommentCreateDto dto = new ClientCommentCreateDto();
        ClientCommentReadDto expected = getStandardClientCommentReadDto();

        ClientCommentReadDto actual = service.create(dto);

        assertThat(actual).isEqualTo(expected);
    }

    private ClientCommentReadDto getStandardClientCommentReadDto() {
        ClientCommentReadDto comment = new ClientCommentReadDto();
        comment.setId(1L);
        AuthorReadDto author = getStandardAuthor();
        comment.setAuthor(author);
        comment.setText(TEXT);
        return comment;
    }

    private AuthorReadDto getStandardAuthor() {
        AuthorReadDto author = new AuthorReadDto();
        author.setFirstName(FIRST_NAME);
        author.setLastName(LAST_NAME);
        return author;
    }

    private CommentReadDto getStandardCommentReadDto() {
        CommentReadDto comment = new CommentReadDto();
        comment.setId(1L);
        comment.setUserId(1L);
        comment.setText(TEXT);
        return comment;
    }

    @Test
    void checkUpdateShouldReturnEquals() {
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(userClient).getByEmail(any());
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setId(1L);
        commentUpdateDto.setUserId(1L);
        commentUpdateDto.setNewsId(1L);
        commentUpdateDto.setText(TEXT);
        doReturn(commentUpdateDto).when(commentMapper).toCommentUpdateDto(any());
        CommentReadDto commentReadDto = getStandardCommentReadDto();
        doReturn(commentReadDto).when(newsClient).updateComment(any(), any());
        ClientCommentReadDto clientCommentReadDto = getStandardClientCommentReadDto();
        doReturn(clientCommentReadDto).when(commentMapper).toClientCommentReadDto(any());
        AuthorReadDto authorReadDto = getStandardAuthor();
        doReturn(authorReadDto).when(authorMapper).toAuthor(any());
        ClientCommentUpdateDto clientCommentUpdateDto = new ClientCommentUpdateDto();
        clientCommentUpdateDto.setId(1L);
        clientCommentUpdateDto.setEmail(EMAIL);
        clientCommentUpdateDto.setNewsId(1L);
        clientCommentUpdateDto.setText(TEXT);
        ClientCommentReadDto expected = getStandardClientCommentReadDto();

        ClientCommentReadDto actual = service.update(clientCommentUpdateDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkDeleteShouldCaptured() {
        prepareSecurityContext(UserRole.ADMIN, 1L);
        CommentReadDto commentReadDto = getStandardCommentReadDto();
        doReturn(commentReadDto).when(newsClient).getCommentById(any());
        service.delete(1L);
        verify(newsClient).deleteCommentById(captor.capture());
        Long captured = captor.getValue();
        assertThat(captured).isEqualTo(1L);
    }

    @Test
    void checkDeleteShouldThrowAuthenticationExc() {
        prepareSecurityContext(UserRole.SUBSCRIBER, 2L);
        CommentReadDto commentReadDto = getStandardCommentReadDto();
        doReturn(commentReadDto).when(newsClient).getCommentById(any());
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

    @Test
    void checkFindAllShouldHasSize2() {
        CommentReadDto comment1 = getStandardCommentReadDto();
        CommentReadDto comment2 = getStandardCommentReadDto();
        List<CommentReadDto> list = List.of(comment1, comment2);
        doReturn(list).when(newsClient).getAllComments(1, 2);
        doReturn(new ClientSimpleCommentReadDto()).when(commentMapper).toSimpleClientReadDto(any());
        int expectedSize = 2;

        List<ClientSimpleCommentReadDto> actual = service.findAll(1, 2);

        assertThat(actual).hasSize(expectedSize);

    }

    @Test
    void checkFindByIdShouldReturnEquals() {
        CommentReadDto commentReadDto = getStandardCommentReadDto();
        doReturn(commentReadDto).when(newsClient).getCommentById(any());
        ClientCommentReadDto clientCommentReadDto = getStandardClientCommentReadDto();
        doReturn(clientCommentReadDto).when(commentMapper).toClientCommentReadDto(any());
        UserDto userDto = getStandardUserDto();
        doReturn(userDto).when(userClient).getById(any());
        AuthorReadDto authorReadDto = getStandardAuthor();
        doReturn(authorReadDto).when(authorMapper).toAuthor(any());
        ClientCommentReadDto expected = getStandardClientCommentReadDto();

        ClientCommentReadDto actual = service.findById(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkFindByParamsShouldHasSize2() {
        CommentReadDto comment1 = getStandardCommentReadDto();
        CommentReadDto comment2 = getStandardCommentReadDto();
        List<CommentReadDto> list = List.of(comment1, comment2);
        QueryParamsComment params = new QueryParamsComment();
        params.setNews_id(1L);
        params.setUser_id(1L);
        params.setText(TEXT);
        doReturn(list).when(newsClient).getCommentByParams(1, 2, params);
        doReturn(new ClientSimpleCommentReadDto()).when(commentMapper).toSimpleClientReadDto(any());
        int expectedSize = 2;

        List<ClientSimpleCommentReadDto> actual = service.findByParams(1, 2, params);

        assertThat(actual).hasSize(expectedSize);
    }
}
