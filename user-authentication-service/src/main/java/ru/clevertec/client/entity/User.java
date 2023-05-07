package ru.clevertec.client.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;

    public enum UserRole {
        ADMIN, JOURNALIST, SUBSCRIBER
    }
}
