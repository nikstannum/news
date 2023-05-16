package ru.clevertec.service.dto;

import lombok.Getter;
import lombok.Setter;
/**
 * The essence of the web interface for generating news search criteria
 */
@Getter
@Setter
public class QueryParamsNews {
    private Long user_id;
    private String title;
    private String text;
}
