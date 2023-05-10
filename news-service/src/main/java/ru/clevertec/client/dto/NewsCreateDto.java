package ru.clevertec.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsCreateDto {
    private Long userId;
    private String title;
    private String text;
}
