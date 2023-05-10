package ru.clevertec.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.clevertec.data.User.UserRole;

@Getter
@Setter
@EqualsAndHashCode
public class UserReadDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}
