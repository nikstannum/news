package ru.clevertec.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * the essence of the web interface for passing parameters for the updated news
 */
@Getter
@Setter
public class ClientNewsUpdateDto {

    @NotNull
    Long id;
    @NotNull
    @Email
    private String email;
    @Size(max = 150, message = "Too long header of article. Max length should be 150 characters.")
    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
