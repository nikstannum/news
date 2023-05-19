package ru.clevertec.service;

import java.util.List;
import ru.clevertec.data.util.NewsQueryParams;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.NewsUpdateDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

/**
 * The interface for news management of the non-public microservice news-service.
 */
public interface NewsService {
    /**
     * Method for creating new news
     *
     * @param news parameters for creating news, such as author's id, news title and news text ({@link ru.clevertec.service.dto.NewsCreateDto})
     * @return created news
     */
    NewsReadDto create(NewsCreateDto news);

    /**
     * Method to get all news. The list of news is returned in paginated mode
     *
     * @param page page number
     * @param size page size
     * @return page of news
     */
    List<SimpleNewsReadDto> findAll(Integer page, Integer size);

    /**
     * Method for receiving news with a paginated list of comments to it
     *
     * @param id   news identifier
     * @param page page number with comments
     * @param size comment page size
     * @return news with page of comments
     */
    NewsReadDto findById(Long id, Integer page, Integer size);

    /**
     * Method for searching news by parameters
     *
     * @param page    page number
     * @param size    page size
     * @param keyWord keyword. Search is carried out by the keyword contained in the title of the news or the text of the news
     * @param params  news search options ({@link ru.clevertec.data.util.NewsQueryParams})
     * @return page of news
     */
    List<SimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, NewsQueryParams params);

    /**
     * Method for updating news
     *
     * @param news parameters for updating the news, such as news identifier, author's id, news title and news text
     *             ({@link ru.clevertec.service.dto.NewsUpdateDto})
     * @return updated news
     */
    NewsReadDto update(NewsUpdateDto news);

    /**
     * deleting news by ID
     *
     * @param id news id
     */
    void deleteById(Long id);
}
