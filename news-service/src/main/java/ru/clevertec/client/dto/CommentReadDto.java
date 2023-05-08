package ru.clevertec.client.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReadDto {

    private Long id;

    private Long userId;

    private String text;

    private Instant createTime;
}
