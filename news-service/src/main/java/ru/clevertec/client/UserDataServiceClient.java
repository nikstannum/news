package ru.clevertec.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.client.dto.UserDto;
import ru.clevertec.client.entity.User;

@FeignClient(name = "user-data-service", url = "http://localhost:8081")
public interface UserDataServiceClient {

    @GetMapping("/api/users/{id}")
    UserDto getById(@PathVariable("id") Long id);

    @GetMapping("/api/users/params")
    UserDto getByEmail(@RequestParam("email") String email);

    @PutMapping("/api/users/ids")
    List<UserDto> getAllUsersByIds(@RequestBody List<Long> ids);
}
