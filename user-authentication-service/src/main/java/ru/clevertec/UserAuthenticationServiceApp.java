package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Public microservice for user authentication (authentication server)
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class UserAuthenticationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthenticationServiceApp.class, args);
    }
}