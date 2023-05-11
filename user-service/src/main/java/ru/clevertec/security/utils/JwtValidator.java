package ru.clevertec.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.clevertec.service.exception.AuthenticationException;

@Component
public class JwtValidator {

    private static final String EXC_MSG_TOKEN_EXPIRED = "Token expired";
    private static final String EXC_MSG_UNSUPPORTED_TOKEN = "Unsupported token";
    private static final String EXC_MSG_MALFORMED_TOKEN = "Malformed token";
    private static final String EXC_MSG_INVALID_SIGNATURE = "Invalid signature";
    private static final String EXC_MSG_INVALID_TOKEN = "Invalid token";

    private final SecretKey jwtAccessSecret;

    public JwtValidator(@Value("${app.jwt.access.secret}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }


    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    private boolean validateToken(String token, Key secret) {
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

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
