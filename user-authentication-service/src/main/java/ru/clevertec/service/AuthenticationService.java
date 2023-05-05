package ru.clevertec.service;

import lombok.NonNull;
import ru.clevertec.entity.JwtToken;
import ru.clevertec.entity.LoginDto;

public interface AuthenticationService {
    JwtToken login(@NonNull LoginDto loginDto);

    JwtToken getAccessToken(String refreshToken);

    JwtToken refresh(String refreshToken);
}
