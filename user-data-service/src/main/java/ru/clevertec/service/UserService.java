package ru.clevertec.service;

import java.util.List;
import ru.clevertec.api.dto.UserCreateDto;
import ru.clevertec.api.dto.UserReadDto;
import ru.clevertec.api.dto.UserSecureDto;
import ru.clevertec.api.dto.UserUpdateDto;

/**
 * The main interface of the business logic of the non-public microservice user-data-service.
 */
public interface UserService {
    /**
     * Method for creating a new user
     *
     * @param user basic user characteristics, such as first name, last name, email, password
     * @return created user. Note: the password value is not displayed
     */
    UserReadDto create(UserCreateDto user);

    /**
     * Method for paginating all objects.
     *
     * @param page page number
     * @param size page size
     * @return object list.  Note: the password value is not displayed
     */
    List<UserReadDto> findAll(Integer page, Integer size);

    /**
     * Method for getting a list of users by their IDs. It is used as a helper method for getting news authors when generating a list of news
     * in a public news service. Note: because the listing of news is paginated, there is no need to get the list of news authors in a paginated.
     * fashion.
     *
     * @param ids list of user ids
     * @return a list of users
     */
    List<UserReadDto> findUsersByIds(List<Long> ids);

    /**
     * Method for getting an object by its id
     *
     * @param id object identifier
     * @return desired object.  Note: the password value is not displayed
     */
    UserReadDto findById(Long id);

    /**
     * Method for getting user by email
     *
     * @param email user email
     * @return desired object.  Note: the password value is not displayed
     */
    UserReadDto findByEmail(String email);

    /**
     * Method to update user information
     *
     * @param userUpdateDto a set of new user information
     * @return updated user.  Note: the password value is not displayed
     */
    UserReadDto update(UserUpdateDto userUpdateDto);

    /**
     * Method for deleting an object by its ID
     *
     * @param id object identifier
     */
    void deleteById(Long id);

    /**
     * Method for getting user by email
     *
     * @param email user email
     * @return desired object. Note: the returned object contains the hashed password
     */
    UserSecureDto findSecureUser(String email);
}
