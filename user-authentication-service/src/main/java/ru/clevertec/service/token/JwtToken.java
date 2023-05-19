package ru.clevertec.service.token;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class JwtToken {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
