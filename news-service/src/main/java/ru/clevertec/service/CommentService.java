package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.ClientCommentCreateDto;
import ru.clevertec.service.dto.ClientCommentReadDto;
import ru.clevertec.service.dto.ClientCommentUpdateDto;
import ru.clevertec.service.dto.QueryParamsComment;
import ru.clevertec.service.dto.ClientSimpleCommentReadDto;

/**
 * The interface of the business logic for comment management of the public microservice news-service.
 */
public interface CommentService {
    /**
     * Method for creating a comment on the news
     *
     * @param clientCommentCreateDto parameters for creating a comment, such as the news identifier, e-mail of the author of the comment and the
     *                               text of the comment ({@link ru.clevertec.service.dto.ClientCommentCreateDto})
     * @return created comment
     */
    ClientCommentReadDto create(ClientCommentCreateDto clientCommentCreateDto);

    /**
     * Method for updating the comment to the news
     *
     * @param clientCommentUpdateDto parameters for updating the comment to the news, such as comment ID news ID comment author e-mail and comment
     *                               text ({@link ru.clevertec.service.dto.ClientCommentUpdateDto})
     * @return
     */
    ClientCommentReadDto update(ClientCommentUpdateDto clientCommentUpdateDto);

    /**
     * Method for deleting a comment by its id
     *
     * @param id comment's id
     */
    void delete(Long id);

    /**
     * Method for getting all comments. Getting a list of comments is implemented in paginated mode
     *
     * @param page page number
     * @param size page size
     * @return comment list
     */
    List<ClientSimpleCommentReadDto> findAll(Integer page, Integer size);

    /**
     * Method for getting a comment by its id
     *
     * @param id comment's id
     * @return comment
     */
    ClientCommentReadDto findById(Long id);

    /**
     * Method for searching comments by parameters. The list of comments is displayed in paginated mode
     *
     * @param page               page number
     * @param size               page size
     * @param queryParamsComment parameters for searching comments such as news ID, author ID, comment text (the word or words contained in the
     *                           comment text) ({@link ru.clevertec.service.dto.QueryParamsComment})
     * @return comment list
     */
    List<ClientSimpleCommentReadDto> findByParams(Integer page, Integer size, QueryParamsComment queryParamsComment);
}
