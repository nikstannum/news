package ru.clevertec.service;

import lombok.NonNull;
import ru.clevertec.service.token.JwtToken;
import ru.clevertec.service.dto.LoginDto;

public interface AuthenticationService {
    JwtToken login(@NonNull LoginDto loginDto);

    JwtToken getAccessToken(String refreshToken);

    JwtToken refresh(String refreshToken);
}
