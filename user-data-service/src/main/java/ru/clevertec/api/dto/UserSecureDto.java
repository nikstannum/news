package ru.clevertec.api.dto;

import lombok.Getter;
import lombok.Setter;
import ru.clevertec.data.User.UserRole;

@Getter
@Setter
public class UserSecureDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
}
