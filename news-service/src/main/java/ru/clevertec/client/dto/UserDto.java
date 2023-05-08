package ru.clevertec.client.dto;

import lombok.Getter;
import lombok.Setter;
import ru.clevertec.client.entity.User.UserRole;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole userRole;
}
