package ru.clevertec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.clevertec.client.entity.Comment;
import ru.clevertec.client.CommentDataServiceClient;
import ru.clevertec.client.entity.User;
import ru.clevertec.client.UserDataServiceClient;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.AuthorReadDto;
import ru.clevertec.service.dto.CommentCreateUpdateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.mapper.AuthorMapper;
import ru.clevertec.service.mapper.CommentMapper;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDataServiceClient commentClient;
    private final UserDataServiceClient userClient;
    private final CommentMapper commentMapper;
    private final AuthorMapper authorMapper;

    @Override
    public CommentReadDto create(CommentCreateUpdateDto commentCreateDto) {
        Comment comment = commentMapper.toComment(commentCreateDto);
        ResponseEntity<Comment> createdEntity = commentClient.create(comment);
        Comment created = createdEntity.getBody();
        return commentMapper.toCommentReadDto(created);
    }

    @Override
    public CommentReadDto update(Long id, CommentCreateUpdateDto commentCreateDto) {
        Comment comment = commentMapper.toComment(commentCreateDto);
        Comment updated = commentClient.update(id, comment);
        return commentMapper.toCommentReadDto(updated);
    }

    @Override
    public void delete(long id) {
        commentClient.deleteById(id);
    }

    @Override
    public List<CommentReadDto> findAll(Integer page, Integer size) {
        List<Comment> commentList = commentClient.getAll(page, size);
        List<Long> usersIds = commentList.stream()
                .map(Comment::getUserId)
                .toList();
        List<User> users = userClient.getAllUsersByIds(usersIds);
        return collectListCommentReadDto(users, commentList);
    }

    @Override
    public List<CommentReadDto> findByParams(Integer page, Integer size, QueryParamsComment queryParamsComment) {
        List<Comment> commentList = commentClient.getByParams(page, size, queryParamsComment);
        List<Long> usersIds = commentList.stream()
                .map(Comment::getUserId)
                .toList();
        List<User> users = userClient.getAllUsersByIds(usersIds);
        return collectListCommentReadDto(users, commentList);
    }

    private List<CommentReadDto> collectListCommentReadDto(List<User> users, List<Comment> comments) {
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        List<CommentReadDto> commentReadDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            User user = userMap.get(comment.getUserId());
            AuthorReadDto author = authorMapper.toDto(user);
            CommentReadDto commentReadDto = commentMapper.toCommentReadDto(comment);
            commentReadDto.setAuthor(author);
            commentReadDtoList.add(commentReadDto);
        }
        return commentReadDtoList;
    }

    @Override
    public CommentReadDto findById(Long id) {
        Comment comment = commentClient.getById(id);
        return commentMapper.toCommentReadDto(comment);
    }
}
