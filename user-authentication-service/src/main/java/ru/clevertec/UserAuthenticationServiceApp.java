package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Public microservice for user authentication (authentication server)
 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableFeignClients
@EnableDiscoveryClient
public class UserAuthenticationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthenticationServiceApp.class, args);
    }
}