package ru.clevertec.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * the essence of the web interface when reading one news item
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientNewsReadDto implements Serializable {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant time;
    List<ClientCommentReadDto> comments;
}
