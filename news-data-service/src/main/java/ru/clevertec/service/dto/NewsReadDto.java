package ru.clevertec.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.clevertec.data.entity.Comment;

@Getter
@Setter
public class NewsReadDto {

    private Long id;
    private Long userId;
    private String title;
    private String text;
    private Instant time;
    private List<CommentReadDto> comments;
}
