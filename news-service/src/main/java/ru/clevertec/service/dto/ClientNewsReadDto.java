package ru.clevertec.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.clevertec.client.entity.Comment;

@Getter
@Setter
public class ClientNewsReadDto implements Serializable {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant time;
    List<Comment> comments;
}
