package ru.clevertec;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.clevertec.security.filter.JwtFilter;
/**
 * Security configuration for microservice
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {

    private static final String[] DOCUMENTATION = {
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers(DOCUMENTATION).permitAll()
                // news
                .requestMatchers(HttpMethod.GET, "/v1/news").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/news/**").permitAll()
                //comments
                .requestMatchers(HttpMethod.GET, "/v1/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/comments/**").permitAll()

                .anyRequest().authenticated().and()
                .build();
    }
}
