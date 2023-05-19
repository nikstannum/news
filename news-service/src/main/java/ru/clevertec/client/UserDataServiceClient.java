package ru.clevertec.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.client.dto.UserDto;

/**
 * Client for sending requests to a non-public user-data service
 */
@FeignClient(name = "user-data-service", configuration = FeignErrorDecoder.class)
public interface UserDataServiceClient {

    @GetMapping("/v1/users/{id}")
    UserDto getById(@PathVariable("id") Long id);

    @GetMapping("/v1/users/params")
    UserDto getByEmail(@RequestParam("email") String email);

    @PutMapping("/v1/users/ids")
    List<UserDto> getAllUsersByIds(@RequestBody List<Long> ids);
}
