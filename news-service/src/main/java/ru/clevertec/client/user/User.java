package ru.clevertec.client.user;

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
    private UserRole userRole;

    public enum UserRole {
        ADMIN, JOURNALIST, SUBSCRIBER
    }
}
