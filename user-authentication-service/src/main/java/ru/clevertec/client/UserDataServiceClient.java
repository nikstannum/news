package ru.clevertec.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.service.dto.UserDto;

/**
 * Client for sending requests to a non-public user-data service
 */
@FeignClient(name = "user-data-service", configuration = FeignErrorDecoder.class)
public interface UserDataServiceClient {

    @PostMapping("/v1/users/secure")
    UserDto getByEmail(@RequestParam("email") String email);
}
