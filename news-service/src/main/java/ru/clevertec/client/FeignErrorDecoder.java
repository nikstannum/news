package ru.clevertec.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import ru.clevertec.service.dto.error.ErrorDto;
import ru.clevertec.service.dto.error.ValidationResultDto;
import ru.clevertec.service.exception.BadRequestException;
import ru.clevertec.service.exception.NotFoundException;
import ru.clevertec.service.exception.ValidationException;
import ru.clevertec.util.logger.LogInvocation;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    @LogInvocation
    public Exception decode(String methodKey, Response response) {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        String error;
        try {
            error = extractJsonError(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String message = extractMessageErrorDto(error);
        switch (response.status()) {
            case 400 -> {
                return new BadRequestException(message);
            }
            case 404 -> {
                return new NotFoundException(message);
            }
            case 422 -> {
                Map<String, List<String>> map = extractMessagesValidationDto(error);
                return new ValidationException(map);
            }
            default -> {
                return new RuntimeException();
            }
        }

    }

    private Map<String, List<String>> extractMessagesValidationDto(String error) {
        try {
            ValidationResultDto dto = objectMapper.readValue(error, ValidationResultDto.class);
            return dto.getMessages();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractMessageErrorDto(String error) {
        ErrorDto errorDto;
        try {
            errorDto = objectMapper.readValue(error, ErrorDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return errorDto.getErrorMessage();
    }

    private String extractJsonError(Response response) throws IOException {
        Reader reader = null;
        String result;
        try {
            reader = response.body().asReader(StandardCharsets.UTF_8);
            result = IOUtils.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }
}
