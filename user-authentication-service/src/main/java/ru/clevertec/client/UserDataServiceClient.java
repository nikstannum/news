package ru.clevertec.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.service.dto.UserDto;

@FeignClient(name = "user-data-service", url = "http://localhost:8081")
public interface UserDataServiceClient {

    @PostMapping("/v1/users/secure")
    UserDto getByEmail(@RequestParam("email") String email);
}
