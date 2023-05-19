package ru.clevertec.client.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Class for creating DTO objects for exchanging data with a non-public user-data-service
 */
@Getter
@Setter
public class CommentUpdateDto {
    private Long id;
    private Long newsId;
    private Long userId;
    private String text;
}
