package ru.clevertec.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
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
import ru.clevertec.service.dto.ClientUserCreateDto;
import ru.clevertec.service.dto.ClientUserReadDto;
import ru.clevertec.service.dto.ClientUserUpdateDto;
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.ValidationException;

@Tag(name = "User", description = "User management APIs")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class RestUserController {

    private static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";

    private final UserService userService;

//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    @Cacheable(value = "User", key = "#id")
//    public ClientUserReadDto getById(@PathVariable Long id) {
////        String currentPrincipalEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        ClientUserReadDto userReadDto = userService.findById(id);
////        if (currentPrincipalEmail.equals(userReadDto.getEmail())) {
//        return userService.findById(id);
//    }
////        throw new RuntimeException("SOMETHING WRONG"); // FIXME
////    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = "User", key = "#id")
    public ClientUserReadDto getById(@PathVariable Long id) {
        return userService.findById(id);
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
////    @LogInvocation
////    @PreAuthorize("hasAuthority('ADMIN')")
//    public List<ClientUserReadDto> getAll(@RequestParam Integer page, @RequestParam Integer size) {
//        return userService.findAll(page, size);
//    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientUserReadDto> getAll(@RequestParam Integer page, @RequestParam Integer size) {
        return userService.findAll(page, size);
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public ClientUserReadDto getByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientUserReadDto> create(@RequestBody @Valid ClientUserCreateDto dto, Errors errors) {
        checkErrors(errors);
        ClientUserReadDto created = processCreate(dto);
        return buildResponseCreated(created);
    }

    @CachePut(value = "User", key = "#id")
    private ClientUserReadDto processCreate(ClientUserCreateDto dto) {
        return userService.create(dto);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<ClientUserReadDto> buildResponseCreated(ClientUserReadDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(ClientUserReadDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/users/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CachePut(value = "User", key = "#id")
    public ClientUserReadDto update(@PathVariable Long id, @RequestBody @Valid ClientUserUpdateDto user, Errors errors) {
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "User", key = "#id")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
