package ru.clevertec.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClientNewsReadDto implements Serializable {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant time;
    List<ClientCommentReadDto> comments;
}
