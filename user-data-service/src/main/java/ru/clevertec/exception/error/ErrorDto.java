package ru.clevertec.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Main class for providing error information
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
    private String errorType;
    private String errorMessage;
}
