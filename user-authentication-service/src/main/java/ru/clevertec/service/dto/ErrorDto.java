package ru.clevertec.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Main class for providing error information
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorDto {

    private String errorType;
    private String errorMessage;
}
