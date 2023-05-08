package ru.clevertec.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.clevertec.service.dto.UserDto;
import ru.clevertec.service.exception.AuthenticationException;

@Component
public class JwtProvider {

    public static final String EXC_MSG_TOKEN_EXPIRED = "Token expired";
    public static final String EXC_MSG_UNSUPPORTED_TOKEN = "Unsupported token";
    public static final String EXC_MSG_MALFORMED_TOKEN = "Malformed token";
    public static final String EXC_MSG_INVALID_SIGNATURE = "Invalid signature";
    public static final String EXC_MSG_INVALID_TOKEN = "Invalid token";
    @Value("${jwt.access.expiration}")
    private int accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(@Value("${jwt.access.secret}") String jwtAccessSecret,
                       @Value("${jwt.refresh.secret}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull UserDto user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(accessExpiration).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("role", user.getRole())
                .claim("email", user.getEmail())
                .compact();
    }

    public String generateRefreshToken(@NonNull UserDto user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(refreshExpiration).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            throw new AuthenticationException(EXC_MSG_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException unsEx) {
            throw new AuthenticationException(EXC_MSG_UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException mjEx) {
            throw new AuthenticationException(EXC_MSG_MALFORMED_TOKEN);
        } catch (SignatureException sEx) {
            throw new AuthenticationException(EXC_MSG_INVALID_SIGNATURE);
        } catch (Exception e) {
            throw new AuthenticationException(EXC_MSG_INVALID_TOKEN);
        }
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
