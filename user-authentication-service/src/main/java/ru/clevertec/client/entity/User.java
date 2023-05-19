package ru.clevertec.client.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;

    @Getter
    @RequiredArgsConstructor
    public enum UserRole implements GrantedAuthority {

        ADMIN("ADMIN"),
        JOURNALIST("JOURNALIST"),
        SUBSCRIBER("SUBSCRIBER");

        private final String value;

        @Override
        public String getAuthority() {
            return value;
        }
    }
}
