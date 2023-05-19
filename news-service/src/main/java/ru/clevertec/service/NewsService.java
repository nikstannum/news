package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;
import ru.clevertec.service.dto.QueryParamsNews;

/**
 * The interface of the business logic for news management of the public microservice news-service.
 */
public interface NewsService {

    /**
     * Method for receiving news with a paginated list of comments to it
     *
     * @param id   news identifier
     * @param page page number with comments
     * @param size comment page size
     * @return news with page of comments
     */
    ClientNewsReadDto findById(Long id, Integer page, Integer size);

    /**
     * Method to get all news. The list of news is returned in paginated mode
     *
     * @param page page number
     * @param size page size
     * @return page of news
     */
    List<ClientSimpleNewsReadDto> findAll(Integer page, Integer size);

    /**
     * Method for searching news by parameters
     *
     * @param page    page number
     * @param size    page size
     * @param keyWord keyword. Search is carried out by the keyword contained in the title of the news or the text of the news
     * @param params  news search options ({@link ru.clevertec.service.dto.QueryParamsNews})
     * @return page of news
     */
    List<ClientSimpleNewsReadDto> findByParams(Integer page, Integer size, String keyWord, QueryParamsNews params);

    /**
     * Method for creating new news
     *
     * @param news parameters for creating news, such as author's email, news title and news text ({@link ru.clevertec.service.dto.ClientNewsCreateDto})
     * @return created news
     */
    ClientNewsReadDto create(ClientNewsCreateDto news);

    /**
     * Method for updating news
     *
     * @param id   news id
     * @param news parameters for updating the news, such as news identifier, author's e-mail, news title and news text ({@link ru.clevertec.service.dto.ClientNewsUpdateDto})
     * @return updated news
     */
    ClientNewsReadDto update(Long id, ClientNewsUpdateDto news);

    /**
     * deleting news by ID
     *
     * @param id news id
     */
    void delete(Long id);
}
