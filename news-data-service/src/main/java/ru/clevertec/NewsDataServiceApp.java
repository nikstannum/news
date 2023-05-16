package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Non-public client microservice for news and comments management
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NewsDataServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(NewsDataServiceApp.class, args);
    }
}