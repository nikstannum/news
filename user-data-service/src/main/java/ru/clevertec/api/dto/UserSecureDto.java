package ru.clevertec.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.clevertec.data.User.UserRole;

/**
 * Class for creating DTO objects for exchanging data with public service (user-authentication-service)
 */
@Getter
@Setter
@EqualsAndHashCode
public class UserSecureDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
}
