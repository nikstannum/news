package ru.clevertec.client.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

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

    @RequiredArgsConstructor
    @Getter
    public enum UserRole implements GrantedAuthority {
        ADMIN("ADMIN"), JOURNALIST("JOURNALIST"), SUBSCRIBER("SUBSCRIBER");

        private final String value;

        @Override
        public String getAuthority() {
            return value;
        }
    }
}
