package ru.clevertec.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clevertec.client.NewsDataServiceClient;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.client.dto.CommentCreateDto;
import ru.clevertec.client.dto.CommentReadDto;
import ru.clevertec.client.dto.CommentUpdateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.SimpleClientCommentReadDto;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.CommentMapper;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserDataServiceClient userClient;
    private final NewsDataServiceClient newsClient;
    private final CommentMapper commentMapper;
    private final AuthorMapper authorMapper;


    @Override
    public ClientCommentReadDto create(ClientCommentCreateDto clientCommentCreateDto) {
        String email = clientCommentCreateDto.getEmail();
        UserDto userDto = userClient.getByEmail(email);
        Long userId = userDto.getId();
        CommentCreateDto commentCreateDto = commentMapper.toCommentCreateDto(clientCommentCreateDto);
        commentCreateDto.setUserId(userId);
        ResponseEntity<CommentReadDto> commentReadDtoResponseEntity = newsClient.createComment(commentCreateDto);
        CommentReadDto commentReadDto = commentReadDtoResponseEntity.getBody();
        ClientCommentReadDto comment = commentMapper.toClientCommentReadDto(commentReadDto);
        AuthorReadDto author = authorMapper.toDto(userDto);
        comment.setAuthor(author);
        return comment;
    }

    @Override
    public ClientCommentReadDto update(Long id, ClientCommentUpdateDto clientCommentUpdateDto) {
        UserDto userDto = userClient.getByEmail(clientCommentUpdateDto.getEmail());
        Long userId = userDto.getId();
        CommentUpdateDto commentUpdateDto = commentMapper.toCommentUpdateDto(clientCommentUpdateDto);
        commentUpdateDto.setUserId(userId);
        CommentReadDto commentReadDto = newsClient.updateComment(id, commentUpdateDto);
        ClientCommentReadDto clientCommentReadDto = commentMapper.toClientCommentReadDto(commentReadDto);
        AuthorReadDto author = authorMapper.toDto(userDto);
        clientCommentReadDto.setAuthor(author);
        return clientCommentReadDto;
    }

    @Override
    public void delete(Long id) {
        newsClient.deleteCommentById(id);
    }

    @Override
    public List<SimpleClientCommentReadDto> findAll(Integer page, Integer size) {
        List<CommentReadDto> commentReadDtoList = newsClient.getAllComments(page, size);
        return commentReadDtoList.stream()
                .map(commentMapper::toSimpleClientReadDto)
                .toList();
    }

    @Override
    public ClientCommentReadDto findById(Long id) {
        CommentReadDto commentReadDto = newsClient.getCommentById(id);
        ClientCommentReadDto clientCommentReadDto = commentMapper.toClientCommentReadDto(commentReadDto);
        Long userId = commentReadDto.getUserId();
        UserDto userDto = userClient.getById(userId);
        AuthorReadDto author = authorMapper.toDto(userDto);
        clientCommentReadDto.setAuthor(author);
        return clientCommentReadDto;
    }

    @Override
    public List<SimpleClientCommentReadDto> findByParams(Integer page, Integer size, QueryParamsComment queryParamsComment) {
        List<CommentReadDto> commentReadDto = newsClient.getCommentByParams(page, size, queryParamsComment);
        return commentReadDto.stream()
                .map(commentMapper::toSimpleClientReadDto)
                .toList();
    }
}
