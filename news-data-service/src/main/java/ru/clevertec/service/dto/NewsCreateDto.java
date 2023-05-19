package ru.clevertec.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for creating DTO objects for exchanging data with public service (news-service)
 */
@Getter
@Setter
public class NewsCreateDto {
    @NotNull
    private Long userId;
    @NotBlank
    @Size(max = 150, message = "Too long header of article. Max length should be 150 characters.")
    private String title;
    @NotBlank
    private String text;
}
