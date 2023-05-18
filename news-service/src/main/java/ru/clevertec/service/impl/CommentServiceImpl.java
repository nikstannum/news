package ru.clevertec.service.impl;

import java.util.Collection;
import java.util.List;
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
import ru.clevertec.client.dto.CommentCreateDto;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.CommentUpdateDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.User.UserRole;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.ClientSimpleCommentReadDto;
import ru.clevertec.service.exception.AuthenticationException;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.CommentMapper;
import ru.clevertec.util.cache.CacheDelete;
import ru.clevertec.util.cache.CacheGet;
import ru.clevertec.util.cache.CachePutPost;
import ru.clevertec.util.logger.LogInvocation;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String EXC_MSG_SOMEONE_ELSE_COMMENT = "You can only delete your own comments";
    private final UserDataServiceClient userClient;
    private final NewsDataServiceClient newsClient;
    private final CommentMapper commentMapper;
    private final AuthorMapper authorMapper;


    @Override
    @CachePutPost
    @CachePut(value = "comment", key = "#result.id")
    @LogInvocation
    public ClientCommentReadDto create(ClientCommentCreateDto clientCommentCreateDto) {
        String email = clientCommentCreateDto.getEmail();
        UserDto userDto = userClient.getByEmail(email);
        Long userId = userDto.getId();
        CommentCreateDto commentCreateDto = commentMapper.toCommentCreateDto(clientCommentCreateDto);
        commentCreateDto.setUserId(userId);
        ResponseEntity<CommentReadDto> commentReadDtoResponseEntity = newsClient.createComment(commentCreateDto);
        CommentReadDto commentReadDto = commentReadDtoResponseEntity.getBody();
        ClientCommentReadDto comment = commentMapper.toClientCommentReadDto(commentReadDto);
        AuthorReadDto author = authorMapper.toAuthor(userDto);
        comment.setAuthor(author);
        return comment;
    }

    @Override
    @CachePutPost
    @CachePut(value = "comment", key = "#clientCommentUpdateDto.id")
    @LogInvocation
    public ClientCommentReadDto update(ClientCommentUpdateDto clientCommentUpdateDto) {
        UserDto userDto = userClient.getByEmail(clientCommentUpdateDto.getEmail());
        Long userId = userDto.getId();
        CommentUpdateDto commentUpdateDto = commentMapper.toCommentUpdateDto(clientCommentUpdateDto);
        commentUpdateDto.setUserId(userId);
        CommentReadDto commentReadDto = newsClient.updateComment(clientCommentUpdateDto.getId(), commentUpdateDto);
        ClientCommentReadDto clientCommentReadDto = commentMapper.toClientCommentReadDto(commentReadDto);
        AuthorReadDto author = authorMapper.toAuthor(userDto);
        clientCommentReadDto.setAuthor(author);
        return clientCommentReadDto;
    }

    @Override
    @CacheDelete
    @CacheEvict(value = "comment", key = "#id")
    @LogInvocation
    public void delete(Long id) {
        CommentReadDto comment = newsClient.getCommentById(id);
        Long authorId = comment.getUserId();
        Long authenticationId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!authenticationId.equals(authorId) && !authorities.contains(UserRole.ADMIN)) {
            throw new AuthenticationException(EXC_MSG_SOMEONE_ELSE_COMMENT);
        }
        newsClient.deleteCommentById(id);
    }

    @Override
    @LogInvocation
    public List<ClientSimpleCommentReadDto> findAll(Integer page, Integer size) {
        List<CommentReadDto> commentReadDtoList = newsClient.getAllComments(page, size);
        return commentReadDtoList.stream()
                .map(commentMapper::toSimpleClientReadDto)
                .toList();
    }

    @Override
    @CacheGet
    @Cacheable(value = "comment")
    @LogInvocation
    public ClientCommentReadDto findById(Long id) {
        CommentReadDto commentReadDto = newsClient.getCommentById(id);
        ClientCommentReadDto clientCommentReadDto = commentMapper.toClientCommentReadDto(commentReadDto);
        Long userId = commentReadDto.getUserId();
        UserDto userDto = userClient.getById(userId);
        AuthorReadDto author = authorMapper.toAuthor(userDto);
        clientCommentReadDto.setAuthor(author);
        return clientCommentReadDto;
    }

    @Override
    @LogInvocation
    public List<ClientSimpleCommentReadDto> findByParams(Integer page, Integer size, QueryParamsComment queryParamsComment) {
        List<CommentReadDto> commentReadDto = newsClient.getCommentByParams(page, size, queryParamsComment);
        return commentReadDto.stream()
                .map(commentMapper::toSimpleClientReadDto)
                .toList();
    }
}
