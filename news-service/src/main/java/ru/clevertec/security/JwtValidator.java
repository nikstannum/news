//package ru.clevertec.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.SignatureException;
//import java.security.Key;
//import javax.crypto.SecretKey;
//import lombok.NonNull;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JwtValidator {
//
//    private final SecretKey jwtAccessSecret;
//
//    public JwtValidator(@Value("${jwt.access.secret}") String jwtAccessSecret) {
//        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
//    }
//
//
//    public boolean validateAccessToken(@NonNull String accessToken) {
//        return validateToken(accessToken, jwtAccessSecret);
//    }
//
//    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secret)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (ExpiredJwtException expEx) { // FIXME logging, process
////            log.error("Token expired", expEx);
//            expEx.printStackTrace();
//        } catch (UnsupportedJwtException unsEx) {
////            log.error("Unsupported jwt", unsEx);
//            unsEx.printStackTrace();
//        } catch (MalformedJwtException mjEx) {
////            log.error("Malformed jwt", mjEx);
//            mjEx.printStackTrace();
//        } catch (SignatureException sEx) {
////            log.error("Invalid signature", sEx);
//            sEx.printStackTrace();
//            throw new RuntimeException();
//        } catch (Exception e) {
////            log.error("invalid token", e);
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public Claims getAccessClaims(@NonNull String token) {
//        return getClaims(token, jwtAccessSecret);
//    }
//
//    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secret)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
