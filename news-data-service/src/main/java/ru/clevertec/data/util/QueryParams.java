package ru.clevertec.data.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueryParams {
    private Long user_id;
    private String title;
    private String text;
}
