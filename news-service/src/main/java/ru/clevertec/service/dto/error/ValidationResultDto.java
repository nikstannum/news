package ru.clevertec.service.dto.error;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
/**
 * Class for presenting information about errors related to the invalidity of the entered data
 */
@Getter
@Setter
public class ValidationResultDto extends ErrorDto {
    private static final String DEFAULT_ERROR_TYPE = "Validation Error";
    private static final String DEFAULT_ERROR_MESSAGE = "Sent data violates validation constraints";
    private Map<String, List<String>> messages;

    public ValidationResultDto() {
        super(DEFAULT_ERROR_TYPE, DEFAULT_ERROR_MESSAGE);
    }

    public ValidationResultDto(Map<String, List<String>> messages) {
        this();
        this.messages = messages;
    }
}
