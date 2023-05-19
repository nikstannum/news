package ru.clevertec.service;

import java.util.List;
import ru.clevertec.data.util.QueryCommentParams;
import ru.clevertec.service.dto.CommentCreateDto;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.CommentUpdateDto;

/**
 * The interface for comments management of the non-public microservice news-service.
 */
public interface CommentService {
    /**
     * Method for creating a comment on the news
     *
     * @param comment parameters for creating a comment, such as the news identifier, author's id of the comment and the
     *                text of the comment ({@link ru.clevertec.service.dto.CommentCreateDto})
     * @return created comment
     */
    CommentReadDto create(CommentCreateDto comment);

    /**
     * Method for getting all comments. Getting a list of comments is implemented in paginated mode
     *
     * @param page page number
     * @param size size page size
     * @return comment list
     */
    List<CommentReadDto> findAll(Integer page, Integer size);

    /**
     * Method for getting a comment by its id
     *
     * @param id comment's id
     * @return comment
     */
    CommentReadDto findById(Long id);

    /**
     * Method for searching comments by parameters. The list of comments is displayed in paginated mode
     *
     * @param page        page number
     * @param size        page size
     * @param queryParams parameters for searching comments such as news ID, author ID, comment text (the word or words contained in the comment
     *                    text) ({@link ru.clevertec.data.util.QueryCommentParams})
     * @return comment list
     */
    List<CommentReadDto> findByParams(Integer page, Integer size, QueryCommentParams queryParams);

    /**
     * Method for updating the comment to the news
     *
     * @param comment parameters for updating the comment to the news, such as comment ID news ID comment author e-mail and comment
     *                text ({@link ru.clevertec.service.dto.CommentUpdateDto})
     * @return updated comment
     */
    CommentReadDto update(CommentUpdateDto comment);

    /**
     * Method for deleting a comment by its id
     *
     * @param id comment's id
     */
    void deleteById(Long id);
}
