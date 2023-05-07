package ru.clevertec.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.clevertec.client.entity.Comment;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsReadDto implements Serializable {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant createTime;
    List<Comment> comments;
}
