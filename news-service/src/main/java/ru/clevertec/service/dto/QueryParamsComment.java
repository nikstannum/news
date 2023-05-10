package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryParamsComment {
    private Long news_id;
    private Long user_id;
    private String text;
}
