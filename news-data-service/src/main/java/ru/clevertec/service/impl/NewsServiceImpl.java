package ru.clevertec.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.clevertec.data.CommentRepository;
import ru.clevertec.data.NewsRepository;
import ru.clevertec.data.entity.Comment;
import ru.clevertec.data.entity.News;
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.data.util.NewsSpecificationBuilder;
import ru.clevertec.dto.NewsCreateDto;
import ru.clevertec.dto.NewsReadDto;
import ru.clevertec.dto.NewsUpdateDto;
import ru.clevertec.dto.SimpleNewsReadDto;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.service.NewsService;
import ru.clevertec.service.mapper.NewsMapper;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private static final String ATTRIBUTE_ID = "id";
    private static final String EXC_MSG_NOT_FOUND_BY_ID = "wasn't found news with id = ";

    private final NewsMapper newsMapper;
    private final NewsRepository newsRepository;
    private final CommentRepository commentRepository;
    private final NewsSpecificationBuilder newsSpecificationBuilder;

    @Override
    public NewsReadDto create(NewsCreateDto newsCreateDto) {
        News news = newsMapper.toNews(newsCreateDto);
        News createdDto = newsRepository.save(news);
        return newsMapper.toNewsReadDto(createdDto);
    }

    @Override
    public List<SimpleNewsReadDto> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        Page<News> newsPage = newsRepository.findAll(pageable);
        return newsPage.map(newsMapper::toSimpleNewsReadDto).toList();
    }

    @Override
    public NewsReadDto findById(Long id, Integer page, Integer size) {
        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + id));
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        List<Comment> comments = commentRepository.findByNewsId(news.getId(), pageable);
        news.setComments(comments);
        return newsMapper.toNewsReadDto(news);
    }

    @Override
    public List<SimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, NewsQueryParams params) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        if (keyWord != null) {
            Page<News> newsPage = newsRepository.findByTitleOrTextContains(keyWord, keyWord, pageable);
            return newsPage.map(newsMapper::toSimpleNewsReadDto).toList();
        }
        Specification<News> specification = newsSpecificationBuilder.getSpecificationSelectNewsByParams(params);
        Page<News> newsPage = newsRepository.findAll(specification, pageable);
        return newsPage.map(newsMapper::toSimpleNewsReadDto).toList();
    }

    @Override
    public NewsReadDto update(NewsUpdateDto newsUpdateDto) {
        News news = newsRepository.findById(newsUpdateDto.getId()).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + newsUpdateDto.getId()));
        news.setUserId(newsUpdateDto.getUserId());
        news.setText(newsUpdateDto.getText());
        news.setTitle(newsUpdateDto.getTitle());
        News updated = newsRepository.save(news);
        Pageable pageable = PageRequest.of(0, 10, Direction.ASC, ATTRIBUTE_ID);
        List<Comment> comments = commentRepository.findByNewsId(updated.getId(), pageable);
        updated.setComments(comments);
        return newsMapper.toNewsReadDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }
}
