package ru.clevertec.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.data.CommentRepository;
import ru.clevertec.data.NewsRepository;
import ru.clevertec.data.entity.Comment;
import ru.clevertec.data.util.CommentSpecificationBuilder;
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.logger.LogInvocation;
import ru.clevertec.service.CommentService;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;
import ru.clevertec.service.mapper.CommentMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    public static final String EXC_MSG_NEWS_ID_MISMATCH = "News ID mismatch";
    private static final String ATTRIBUTE_ID = "id";
    private static final String EXC_MSG_NOT_FOUND_BY_ID = "wasn't found comment with id = ";
    private static final String EXC_MSG_NEWS_NOT_FOUND = "Error creation comment. May be given news was deleted";
    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final CommentMapper mapper;
    private final CommentSpecificationBuilder specificationBuilder;

    @Override
    @LogInvocation
    public CommentReadDto create(CommentCreateDto commentCreateDto) {
        Long newsId = commentCreateDto.getNewsId();
        if (!newsRepository.existsById(newsId)) {
            throw new NotFoundException(EXC_MSG_NEWS_NOT_FOUND + newsId);
        }
        Comment comment = mapper.toComment(commentCreateDto);
        Comment created = commentRepository.save(comment);
        return mapper.toCommentReadDto(created);
    }

    @Override
    @LogInvocation
    public List<CommentReadDto> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        Page<Comment> commentPage = commentRepository.findAll(pageable);
        return commentPage.map(mapper::toCommentReadDto).toList();
    }

    @Override
    @LogInvocation
    public CommentReadDto findById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + id));
        return mapper.toCommentReadDto(comment);
    }

    @Override
    @LogInvocation
    public List<CommentReadDto> findByParams(Integer page, Integer size, QueryCommentParams queryParams) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        Specification<Comment> specification = specificationBuilder.getSpecificationSelectCommentByParams(queryParams);
        Page<Comment> commentPage = commentRepository.findAll(specification, pageable);
        return commentPage.map(mapper::toCommentReadDto).toList();
    }

    @Override
    @LogInvocation
    public CommentReadDto update(CommentUpdateDto commentUpdateDto) {
        Long newsId = commentUpdateDto.getNewsId();
        if (!newsRepository.existsById(newsId)) {
            throw new NotFoundException(EXC_MSG_NEWS_NOT_FOUND + newsId);
        }
        Long commentId = commentUpdateDto.getId();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + commentId));
        if (!comment.getNewsId().equals(newsId)) {
            throw new BadRequestException(EXC_MSG_NEWS_ID_MISMATCH);
        }
        comment.setText(commentUpdateDto.getText());
        Comment updated = commentRepository.save(comment);
        return mapper.toCommentReadDto(updated);
    }

    @Override
    @LogInvocation
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
