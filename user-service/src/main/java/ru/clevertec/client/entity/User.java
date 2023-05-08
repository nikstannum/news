package ru.clevertec.client.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Getter
    @RequiredArgsConstructor
    public enum UserRole /*implements GrantedAuthority*/ {
        ADMIN("ADMIN"), JOURNALIST("JOURNALIST"), SUBSCRIBER("SUBSCRIBER");


        private final String value;


//        @Override
//        public String getAuthority() {
//            return value;
//        }
    }
}
