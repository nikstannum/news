package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class QueryParamsNews {
    private Long user_id;
    private String title;
    private String text;
}
