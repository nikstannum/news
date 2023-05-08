package ru.clevertec.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsUpdateDto {
    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    @NotBlank
    @Size(max = 150, message = "Too long header of article. Max length should be 150 characters.")
    private String title;
    @NotBlank
    private String text;
}
