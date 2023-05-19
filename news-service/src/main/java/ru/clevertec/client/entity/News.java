package ru.clevertec.client.entity;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class News {
    private Long id;
    private Long userId;
    private String title;
    private String text;
    private Instant time;
    private List<Comment> comments;
}
