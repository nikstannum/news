package ru.clevertec.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.entity.JwtToken;
import ru.clevertec.entity.LoginDto;
import ru.clevertec.entity.RefreshJwtTokenDto;
import ru.clevertec.service.AuthenticationService;

@RestController
@RequestMapping("/v1/security")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/login")
    public JwtToken login(@RequestBody LoginDto loginDto) {
        JwtToken login = service.login(loginDto);
        return login;
    }

    @PostMapping("/token")
    public JwtToken getNewAccessToken(@RequestBody RefreshJwtTokenDto request) {
        return service.getAccessToken(request.getRefreshToken());
    }

    @PostMapping("/refresh")
    public JwtToken getNewRefreshToken(@RequestBody RefreshJwtTokenDto request) {
        return service.refresh(request.getRefreshToken());
    }

}
