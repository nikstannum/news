package ru.clevertec.service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.clevertec.client.entity.User;

/**
 * Class for generating access token and refresh token
 */
@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    @Value("${jwt.access.expiration}")
    private int accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;

    public JwtProvider(@Value("${jwt.access.secret}") String jwtAccessSecret,
                       @Value("${jwt.refresh.secret}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    /**
     * Method for generating an access token
     *
     * @param user user for whom the access token is generated
     * @return generated access token
     */
    public String generateAccessToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now.plusMinutes(accessExpiration).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .claim("email", user.getEmail())
                .compact();
    }

    /**
     * Method for generating a refresh token
     *
     * @param user user for whom the refresh token is generated
     * @return generated refresh token
     */
    public String generateRefreshToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        Instant refreshExpirationInstant = now.plusDays(refreshExpiration).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }
}
