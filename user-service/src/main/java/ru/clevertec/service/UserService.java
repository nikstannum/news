package ru.clevertec.service;

import java.util.List;
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;

/**
 * The main interface of the business logic of the public microservice user-service.
 */
public interface UserService {

    /**
     * Method for getting an object by its id
     *
     * @param id object identifier
     * @return desired object.  Note: the password value is not displayed
     */
    ClientUserReadDto findById(Long id);

    /**
     * Method for paginating all objects.
     *
     * @param page page number
     * @param size page size
     * @return object list.  Note: the password value is not displayed
     */
    List<ClientUserReadDto> findAll(Integer page, Integer size);

    /**
     * Method for getting user by email
     *
     * @param email user email
     * @return desired object.  Note: the password value is not displayed
     */
    ClientUserReadDto findByEmail(String email);

    /**
     * Method for creating a new user. Before sending data for saving, the password is hashed using the password-hashing function bcrypt.
     *
     * @param dto basic user characteristics, such as first name, last name, email, password
     * @return created user. Note: the password value is not displayed
     */
    ClientUserReadDto create(ClientUserCreateDto dto);

    /**
     * Method to update user information. Before sending data for updating, the password is hashed using the password-hashing function bcrypt.
     *
     * @param user a set of new user information
     * @return updated user.  Note: the password value is not displayed
     */
    ClientUserReadDto update(ClientUserUpdateDto user);

    /**
     * Method for deleting an object by its ID
     *
     * @param id object identifier
     */
    void delete(Long id);
}
