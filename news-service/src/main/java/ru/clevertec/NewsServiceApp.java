package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Public client microservice for news and comments management
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCaching
public class NewsServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApp.class, args);
    }
}