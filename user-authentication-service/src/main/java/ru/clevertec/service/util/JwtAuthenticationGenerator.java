package ru.clevertec.service.util;

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
     * @return a {@link ru.clevertec.service.util.JwtAuthentication} object
     */
    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        String roleStr = claims.get("role", String.class);
        UserRole role = UserRole.valueOf(roleStr);
        jwtInfoToken.setRole(role);
        jwtInfoToken.setEmail(claims.get("email", String.class));
        jwtInfoToken.setId(claims.get("id", Long.class));
        return jwtInfoToken;
    }
}
