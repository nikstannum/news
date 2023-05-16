package ru.clevertec.security.utils;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.clevertec.client.entity.User.UserRole;

/**
 * Auxiliary class for generating an authentication object
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtAuthenticationGenerator {
    /**
     * Method  for generating an authentication object
     *
     * @param claims a JWT claims
     * @return a {@link ru.clevertec.security.utils.JwtAuthentication} object
     */
    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication authentication = new JwtAuthentication();
        String roleStr = claims.get("role", String.class);
        UserRole role = UserRole.valueOf(roleStr);
        authentication.setRole(role);
        authentication.setEmail(claims.get("email", String.class));
        authentication.setId(claims.get("id", Long.class));
        return authentication;
    }
}
