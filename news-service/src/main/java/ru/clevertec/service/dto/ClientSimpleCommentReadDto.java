package ru.clevertec.service.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * web interface entity when getting a list of comments
 */
@Getter
@Setter
public class ClientSimpleCommentReadDto {

    private Long id;

    private Long userId;

    private String text;

    private Instant createTime;
}
