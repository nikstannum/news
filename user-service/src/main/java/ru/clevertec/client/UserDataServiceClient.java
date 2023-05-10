package ru.clevertec.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.client.dto.UserCreateDto;
import ru.clevertec.client.dto.UserReadDto;
import ru.clevertec.client.dto.UserUpdateDto;

@FeignClient(name = "user-data-service", url = "http://localhost:8081", configuration = FeignErrorDecoder.class)
public interface UserDataServiceClient {

    @GetMapping("/api/users/{id}")
    UserReadDto getById(@PathVariable("id") Long id);

    @GetMapping("/api/users")
    List<UserReadDto> getAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/users/params")
    UserReadDto getByEmail(@RequestParam("email") String email);

    @PostMapping("/api/users")
    ResponseEntity<UserReadDto> create(@RequestBody UserCreateDto user);

    @PutMapping("/api/users/{id}")
    UserReadDto update(@PathVariable("id") Long id, @RequestBody UserUpdateDto user);

    @DeleteMapping("/api/users/{id}")
    void deleteById(@PathVariable("id") Long id);
}
