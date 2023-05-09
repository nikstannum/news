package ru.clevertec.service.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import ru.clevertec.client.entity.User.UserRole;

@Getter
@Setter
@NoArgsConstructor
@ToString
@RedisHash("User")
public class ClientUserReadDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}
