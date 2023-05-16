package ru.clevertec.data.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * The essence of the web interface for the formation of search criteria for comments
 */
@Getter
@Setter
@NoArgsConstructor
public class QueryCommentParams {
    private Long news_id;
    private Long user_id;
    private String text;
}
