package ru.clevertec.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtToken {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
