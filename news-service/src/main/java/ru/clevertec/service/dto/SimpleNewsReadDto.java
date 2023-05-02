package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleNewsReadDto {
    Long id;
    Long authorId;
    String title;
    String text;
    Instant createTime;
}
