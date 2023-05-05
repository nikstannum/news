//package ru.clevertec.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import ru.clevertec.security.filter.JwtFilter;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtFilter jwtFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .authorizeHttpRequests()
//                // news
//                .requestMatchers(HttpMethod.POST, "/v1/news").hasAnyAuthority("ADMIN", "JOURNALIST")
//                .requestMatchers(HttpMethod.PUT, "/v1/news/**").hasAnyAuthority("ADMIN", "JOURNALIST")
//                .requestMatchers(HttpMethod.DELETE, "/v1/news/**").hasAnyAuthority("ADMIN", "JOURNALIST")
//
//
//                .requestMatchers(HttpMethod.POST, "/v1/comments").hasAnyAuthority("ADMIN", "SUBSCRIBER")
//                .requestMatchers(HttpMethod.PUT, "/v1/comments/**").hasAnyAuthority("ADMIN", "SUBSCRIBER")
//                .requestMatchers(HttpMethod.DELETE, "/v1/comments/**").hasAnyAuthority("ADMIN", "SUBSCRIBER")
//
//
//                .requestMatchers(HttpMethod.GET, "/v1/news").permitAll()
//                .requestMatchers(HttpMethod.GET, "/v1/news/**/comments").permitAll()
//                .anyRequest().denyAll().and()
//                //comments
//                .build();
//    }
//}
