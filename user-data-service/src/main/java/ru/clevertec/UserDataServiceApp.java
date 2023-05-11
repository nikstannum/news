package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableDiscoveryClient
public class UserDataServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(UserDataServiceApp.class, args);
    }
}