package ru.clevertec.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * web interface entity for passing comment parameters when it is updated
 */
@Getter
@Setter
public class ClientCommentUpdateDto {

    @NotNull
    Long id;
    @NotNull
    private Long newsId;
    @NotNull
    @Email
    private String email;
    @NotBlank
    private String text;
}
