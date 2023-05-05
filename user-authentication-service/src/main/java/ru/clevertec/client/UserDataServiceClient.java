package ru.clevertec.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.entity.User;

@FeignClient(name = "user-data-service", url = "http://localhost:8081")
public interface UserDataServiceClient {

    @GetMapping("/api/users/params")
    User getByEmail(@RequestParam("email") String email);
}
