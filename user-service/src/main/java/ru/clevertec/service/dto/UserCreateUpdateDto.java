package ru.clevertec.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.clevertec.client.entity.User.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateUpdateDto {
    @Size(max = 30, message = "Your first name is longer than 30 characters. You can use an alias.")
    private String firstName;
    @Size(max = 30, message = "Your last name is longer than 30 characters. You can use an alias.")
    private String lastName;
    @Size(max = 50, message = "Your email is longer than 50 characters. You can create a new mailbox and use it.")
    @NotBlank
    @Email
    private String email;
    @Size(min = 6, message = "Too short password.")
    private String password;
    private UserRole role;
}
