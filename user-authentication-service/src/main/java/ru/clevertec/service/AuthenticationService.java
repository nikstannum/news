package ru.clevertec.service;

import lombok.NonNull;
import ru.clevertec.service.dto.LoginDto;
import ru.clevertec.service.token.JwtToken;

/**
 * The main business logic interface of the authentication server
 */
public interface AuthenticationService {

    /**
     * Method for authenticating a user and providing him with a JWT token
     *
     * @param loginDto parameters for authentication (login (email) and password)
     * @return JWT token
     */
    JwtToken login(LoginDto loginDto);

    /**
     * Method for getting a new access token.
     *
     * @param refreshToken refresh token
     * @return JWT token containing the new access token
     */
    JwtToken getAccessToken(String refreshToken);

    /**
     * Method for getting a new JWT token
     *
     * @param refreshToken refresh token
     * @return JWT token containing the new access and the new refresh tokens
     */
    JwtToken refresh(String refreshToken);
}
