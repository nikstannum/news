package ru.clevertec.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-data-service", url = "http://localhost:8081")
public interface UserDataServiceClient {

    @GetMapping("/api/users/{id}")
    User getById(@PathVariable("id") Long id);
}
