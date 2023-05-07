package ru.clevertec.web;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.clevertec.service.UserService;
import ru.clevertec.service.dto.UserCreateUpdateDto;
import ru.clevertec.service.dto.UserReadDto;
import ru.clevertec.service.exception.ValidationException;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = "User", key = "#id")
    public UserReadDto getById(@PathVariable Long id) {
//        String currentPrincipalEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReadDto userReadDto = userService.findById(id);
//        if (currentPrincipalEmail.equals(userReadDto.getEmail())) {
        return userService.findById(id);
    }
//        throw new RuntimeException("SOMETHING WRONG"); // FIXME
//    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
//    @LogInvocation
//    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserReadDto> getAll(@RequestParam Integer page, @RequestParam Integer size) {
        return userService.findAll(page, size);
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public UserReadDto getByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserReadDto> create(@RequestBody @Valid UserCreateUpdateDto dto, Errors errors) {
        checkErrors(errors);
        UserReadDto created = processCreate(dto);
        return buildResponseCreated(created);
    }

    @CachePut(value = "User", key = "#id")
    private UserReadDto processCreate(UserCreateUpdateDto dto) {
        return userService.create(dto);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<UserReadDto> buildResponseCreated(UserReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(UserReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/users/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CachePut(value = "User", key = "#id")
    public UserReadDto update(@PathVariable Long id, @RequestBody @Valid UserCreateUpdateDto user, Errors errors) {
        checkErrors(errors);
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "User", key = "#id")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
