package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ClientCommentReadDto {
    private Long id;
    private AuthorReadDto author;
    private String text;
    private Instant createTime;
}
