package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReadDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserReadRoleDto role;

    public enum UserReadRoleDto {
        ADMIN, JOURNALIST, SUBSCRIBER
    }

}
