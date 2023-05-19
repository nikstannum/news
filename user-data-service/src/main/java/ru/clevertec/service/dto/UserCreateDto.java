package ru.clevertec.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for creating DTO objects for exchanging data with public service (user-service)
 */
@Getter
@Setter
public class UserCreateDto {
    @Size(max = 30, message = "Your first name is longer than 30 characters. You can use an alias.")
    private String firstName;
    @Size(max = 30, message = "Your last name is longer than 30 characters. You can use an alias.")
    private String lastName;
    @Email
    @Size(max = 50, message = "Your email is longer than 50 characters. You can create a new mailbox and use it.")
    private String email;
    @Size(min = 6, message = "Too short password.")
    private String password;
}
