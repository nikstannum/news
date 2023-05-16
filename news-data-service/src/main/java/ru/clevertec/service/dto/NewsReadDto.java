package ru.clevertec.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
/**
 * Class for creating DTO objects for exchanging data with public service (news-service)
 */
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
