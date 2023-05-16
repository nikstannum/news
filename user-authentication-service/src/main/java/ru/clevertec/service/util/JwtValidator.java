package ru.clevertec.service.util;

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

/**
 * Auxiliary class for token validation
 */
@Component
public class JwtValidator {

    private static final String EXC_MSG_TOKEN_EXPIRED = "Token expired";
    private static final String EXC_MSG_UNSUPPORTED_TOKEN = "Unsupported token";
    private static final String EXC_MSG_MALFORMED_TOKEN = "Malformed token";
    private static final String EXC_MSG_INVALID_SIGNATURE = "Invalid signature";
    private static final String EXC_MSG_INVALID_TOKEN = "Invalid token";

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtValidator(@Value("${jwt.access.secret}") String jwtAccessSecret,
                        @Value("${jwt.refresh.secret}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    /**
     * Method for determining the validity of an access token.
     *
     * @param accessToken access token passed by the user
     * @return true if the token is valid
     */
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    /**
     * Method for determining the validity of a refresh token.
     *
     * @param refreshToken refresh token passed by the user
     * @return true if the token is valid
     */
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
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

    /**
     * Method for getting claims from access token payload
     *
     * @param token access token
     * @return a JWT claims set
     */
    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    /**
     * Method for getting claims from refresh token payload
     *
     * @param token refresh token
     * @return a JWT claims set
     */
    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
