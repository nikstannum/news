package ru.clevertec.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.clevertec.data.User.UserRole;

/**
 * Class for creating DTO objects for exchanging data with public services (user-service, news-service)
 */
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
