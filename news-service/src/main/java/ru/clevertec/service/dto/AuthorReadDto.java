package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorReadDto {
    private String email;
    private String firstName;
    private String lastName;
}
