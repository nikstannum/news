package ru.clevertec.security.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtToken {
    private String refreshToken;
}
