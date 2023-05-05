package ru.clevertec.client.user;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-data-service", url = "http://localhost:8081")
public interface UserDataServiceClient {

    @GetMapping("/api/users/{id}")
    User getById(@PathVariable("id") Long id);

    @PutMapping("/api/users/ids")
    List<User> getAllUsersByIds(@RequestBody List<Long> ids);
}
