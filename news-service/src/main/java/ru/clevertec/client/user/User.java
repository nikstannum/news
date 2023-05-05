package ru.clevertec.client.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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

    @RequiredArgsConstructor
    public enum UserRole /*implements GrantedAuthority */ {
        ADMIN("ADMIN"), JOURNALIST("JOURNALIST"), SUBSCRIBER("SUBSCRIBER");

        private final String value;

//        @Override
//        public String getAuthority() {
//            return value;
//        }
    }
}
