package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Non-public service for user management
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserDataServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(UserDataServiceApp.class, args);
    }
}
