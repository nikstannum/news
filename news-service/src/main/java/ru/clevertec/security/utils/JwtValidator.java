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
import lombok.NonNull;
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

    public JwtValidator(@Value("${app.jwt.access.secret}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    /**
     * Method for determining the validity of a token.
     *
     * @param accessToken access token passed by the user
     * @return true if the token is valid
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(accessToken);
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
     * Method for getting claims from token payload
     *
     * @param token access token
     * @return a JWT claims set
     */
    public Claims getAccessClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtAccessSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
