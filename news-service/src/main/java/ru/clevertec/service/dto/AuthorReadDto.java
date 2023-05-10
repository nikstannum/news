package ru.clevertec.service.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorReadDto implements Serializable {
    private String firstName;
    private String lastName;
}
