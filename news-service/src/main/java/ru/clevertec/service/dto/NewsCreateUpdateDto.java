package ru.clevertec.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewsCreateUpdateDto {
    @NotNull
    private Long userId;
    @Size(max = 150, message = "Too long header of article. Max length should be 150 characters.")
    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
