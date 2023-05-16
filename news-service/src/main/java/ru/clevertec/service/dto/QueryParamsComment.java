package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * The essence of the web interface for the formation of search criteria for comments
 */
@Getter
@Setter
public class QueryParamsComment {
    private Long news_id;
    private Long user_id;
    private String text;
}
