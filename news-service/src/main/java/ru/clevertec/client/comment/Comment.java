package ru.clevertec.client.comment;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long newsId;
    private Long userId;
    private String text;
    private Instant createTime;
}
