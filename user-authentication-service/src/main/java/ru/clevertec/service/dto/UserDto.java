package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.clevertec.client.entity.User.UserRole;

/**
 * DTO for interacting with a non-public user-data-service
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
}
