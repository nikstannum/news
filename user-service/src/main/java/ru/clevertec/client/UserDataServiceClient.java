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

@FeignClient(name = "user-data-service", url = "http://localhost:8081")
public interface UserDataServiceClient {

    @GetMapping("/api/users/{id}")
    User getById(@PathVariable("id") Long id);

    @GetMapping("/api/users")
    List<User> getAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping("/api/users/params")
    User getByEmail(@RequestParam("email") String email);

    @PostMapping("/api/users")
    ResponseEntity<User> create(@RequestBody User user);

    @PutMapping("/api/users/{id}")
    User update(@PathVariable("id") Long id, @RequestBody User user);

    @DeleteMapping("/api/users/{id}")
    void deleteById(@PathVariable("id") Long id);
}
