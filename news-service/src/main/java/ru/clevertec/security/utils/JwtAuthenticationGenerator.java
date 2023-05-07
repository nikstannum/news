//package ru.clevertec.security.utils;
//
//import io.jsonwebtoken.Claims;
//import lombok.AccessLevel;
//import lombok.NoArgsConstructor;
//import ru.clevertec.client.entity.User.UserRole;
//import ru.clevertec.security.JwtAuthentication;
//
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
//public final class JwtAuthenticationGenerator {
//    public static JwtAuthentication generate(Claims claims) {
//        JwtAuthentication jwtInfoToken = new JwtAuthentication();
//        String roleStr = claims.get("role", String.class);
//        UserRole role = UserRole.valueOf(roleStr);
//        jwtInfoToken.setRole(role);
//        jwtInfoToken.setEmail(claims.get("email", String.class));
//        return jwtInfoToken;
//    }
//}
