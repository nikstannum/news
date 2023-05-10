package ru.clevertec.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateDto {
    private Long id;
    private Long newsId;
    private Long userId;
    private String text;
}
