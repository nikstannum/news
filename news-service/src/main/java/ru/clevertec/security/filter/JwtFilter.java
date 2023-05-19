package ru.clevertec.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ru.clevertec.security.utils.JwtAuthentication;
import ru.clevertec.security.utils.JwtAuthenticationGenerator;
import ru.clevertec.security.utils.JwtValidator;

/**
 * Filter requests from system users. When a valid token is transferred, the user is authenticated and empowered.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String token = getTokenFromRequest((HttpServletRequest) request);
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
