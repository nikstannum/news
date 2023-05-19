package ru.clevertec.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for creating DTO objects for exchanging data with public service (news-service)
 */
@Getter
@Setter
public class CommentUpdateDto {
    @NotNull
    private Long id;
    @NotNull
    private Long newsId;
    @NotNull
    private Long userId;
    @NotBlank
    private String text;
}
