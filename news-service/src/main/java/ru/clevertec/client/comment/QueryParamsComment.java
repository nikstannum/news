package ru.clevertec.client.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueryParamsComment {
    private Long news_id;
    private Long user_id;
    private String text;
}
