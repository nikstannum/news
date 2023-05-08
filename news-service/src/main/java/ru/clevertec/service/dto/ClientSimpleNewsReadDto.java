package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientSimpleNewsReadDto {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant time;
}
