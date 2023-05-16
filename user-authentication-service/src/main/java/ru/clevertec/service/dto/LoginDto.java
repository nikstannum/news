package ru.clevertec.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for creating objects containing credentials
 */
@Setter
@Getter
public class LoginDto {
    @Email
    private String email;
    @Size(min = 6, message = "Too short password.")
    private String password;
}
