package ru.clevertec.data.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * The essence of the web interface for the formation of search criteria for news
 */
@Getter
@Setter
@NoArgsConstructor
public class NewsQueryParams {
    private Long user_id;
    private String title;
    private String text;
}
