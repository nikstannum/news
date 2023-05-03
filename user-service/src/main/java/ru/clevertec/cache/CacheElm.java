package ru.clevertec.cache;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheElm {
    private Integer quantityUse;
    private LocalDateTime lastTimeUse;
    private Object value;
}
