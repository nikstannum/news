package ru.clevertec.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.service.AuthenticationService;
import ru.clevertec.service.dto.LoginDto;
import ru.clevertec.service.exception.ValidationException;
import ru.clevertec.service.token.JwtToken;
import ru.clevertec.service.token.RefreshJwtToken;

@RestController
@RequestMapping("/v1/security")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public JwtToken login(@RequestBody @Valid LoginDto loginDto, Errors errors) {
        checkErrors(errors);
        return service.login(loginDto);
    }

    private void checkErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
    }

    @PostMapping("/token")
    public JwtToken getNewAccessToken(@RequestBody RefreshJwtToken request) {
        return service.getAccessToken(request.getRefreshToken());
    }

    @PostMapping("/refresh")
    public JwtToken getNewRefreshToken(@RequestBody RefreshJwtToken request) {
        return service.refresh(request.getRefreshToken());
    }

}
