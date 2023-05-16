package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * the essence of the web interface when receiving a list of news
 */
@Getter
@Setter
public class ClientSimpleNewsReadDto {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant time;
}
