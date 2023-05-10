package ru.clevertec.service.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientCommentReadDto implements Serializable {
    private Long id;
    private AuthorReadDto author;
    private String text;
    private Instant createTime;
}
