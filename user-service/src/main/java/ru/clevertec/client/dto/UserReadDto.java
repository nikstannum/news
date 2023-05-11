package ru.clevertec.client.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.clevertec.client.entity.User.UserRole;

@Getter
@Setter
@ToString
public class UserReadDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}
