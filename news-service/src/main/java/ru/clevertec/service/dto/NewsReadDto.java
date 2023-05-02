package ru.clevertec.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.clevertec.client.comment.Comment;

@Getter
@Setter
@NoArgsConstructor
public class NewsReadDto {
    Long id;
    AuthorReadDto author;
    String title;
    String text;
    Instant createTime;
    List<Comment> comments;
}
