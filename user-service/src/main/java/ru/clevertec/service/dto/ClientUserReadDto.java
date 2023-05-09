package ru.clevertec.service.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.clevertec.client.entity.User.UserRole;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClientUserReadDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}
