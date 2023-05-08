package ru.clevertec.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
import ru.clevertec.data.User;
import ru.clevertec.data.UserRepository;
import ru.clevertec.exception.BadRequestException;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.exception.SuchEntityExistsException;
import ru.clevertec.exception.ValidationException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    public static final String EXC_MSG_NOT_FOUND_BY_ID = "wasn't found user with id = ";
    public static final String EXC_MSG_NOT_FOUND_BY_EMAIL = "wasn't found user with email ";
    public static final String EXC_MSG_ID_NOT_MATCH = "Incoming id in body doesn't match path";
    public static final String ATTRIBUTE_ID = "id";
    public static final String EXC_MSG_EMAIL_EXISTS = "Already exists user with email ";

    private final UserRepository userRepository;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@RequestBody @Valid User user, Errors errors) {
        checkErrors(errors);
        Optional<User> existingOpt = userRepository.findUserByEmail(user.getEmail());
        if (existingOpt.isPresent()) {
            throw new SuchEntityExistsException(EXC_MSG_EMAIL_EXISTS + user.getEmail());
        }
        User created = userRepository.save(user);
        return buildResponseCreated(created);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    private ResponseEntity<User> buildResponseCreated(User created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(User created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/users/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll(@RequestParam Integer page, @RequestParam Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Direction.ASC, ATTRIBUTE_ID);
        return userRepository.findAll(pageable).toList();
    }

    @PutMapping("/ids")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findUsersByIds(@RequestBody List<Long> ids) {
        return userRepository.findAllById(ids);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User findById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_ID + id));
    }

    @GetMapping("/params")
    @ResponseStatus(HttpStatus.OK)
    public User findByEmail(@RequestParam String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(EXC_MSG_NOT_FOUND_BY_EMAIL + email));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User update(@PathVariable Long id, @RequestBody @Valid User user, Errors errors) {
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestException(EXC_MSG_ID_NOT_MATCH);
        }
        checkErrors(errors);
        Optional<User> existing = userRepository.findUserByEmail(user.getEmail());
        if (existing.isEmpty() || existing.get().getId().equals(id)) {
            return userRepository.save(user);
        } else {
            throw new SuchEntityExistsException(EXC_MSG_EMAIL_EXISTS + user.getEmail());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
