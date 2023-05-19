package ru.clevertec.client.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long id;
    private Long userId;
    private String text;
    private Instant createTime;
}
