package ru.clevertec.client.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleNewsReadDto {
    private Long id;
    private Long userId;
    private String title;
    private String text;
    private Instant time;
}
