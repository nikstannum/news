package ru.clevertec.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsUpdateDto {
    private Long id;
    private Long userId;
    private String title;
    private String text;
}
