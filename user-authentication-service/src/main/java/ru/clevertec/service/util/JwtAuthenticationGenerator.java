package ru.clevertec.service.util;

import io.jsonwebtoken.Claims;
import lombok.NoArgsConstructor;
import ru.clevertec.client.entity.User.UserRole;

@NoArgsConstructor
public final class JwtAuthenticationGenerator {

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
