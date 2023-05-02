package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentReadDto {
    private Long id;
    private Long newsId;
    private AuthorReadDto author;
    private String text;
    private Instant createTime;
}
