package ru.clevertec.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateUpdateDto {
    @NotNull
    private Long newsId;
    @NotNull
    private Long userId;
    @NotBlank
    private String text;
}
