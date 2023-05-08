package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleClientCommentReadDto {

    private Long id;

    private Long userId;

    private String text;

    private Instant createTime;
}
