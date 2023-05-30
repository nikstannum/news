package ru.clevertec.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.clevertec.security.utils.JwtAuthentication;
import ru.clevertec.security.utils.JwtAuthenticationGenerator;
import ru.clevertec.security.utils.JwtValidator;

/**
 * Filter requests from system users. When a valid token is transferred, the user is authenticated and empowered.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends HttpFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_ = "Bearer ";
    private static final int BEGIN_INDEX = 7;

    private final JwtValidator validator;

    /**
     * Method for filtering requests in a filter embedded in a filter chain.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this filter to pass the request and response
     *                 to for further processing
     * @throws IOException      if an I/O related error has occurred during the processing
     * @throws ServletException if an exception has occurred that interferes with the filterChain's normal operation
     */
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String token = getTokenFromRequest(request);
        if (token != null && validator.validateAccessToken(token)) {
            Claims claims = validator.getAccessClaims(token);
            JwtAuthentication authentication = JwtAuthenticationGenerator.generate(claims);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_)) {
            return bearer.substring(BEGIN_INDEX);
        }
        return null;
    }
}
