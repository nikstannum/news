package ru.clevertec.client.news;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueryParamsNews {
    private Long user_id;
    private String title;
    private String text;
}
