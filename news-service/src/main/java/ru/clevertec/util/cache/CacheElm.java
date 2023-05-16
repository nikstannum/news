package ru.clevertec.util.cache;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Auxiliary class for working with LFU cache. The class object contains information about the number of operations performed with the object in the cache, the time of the last operation on the object, and the object itself.
 */
@Getter
@Setter
public class CacheElm {
    private Integer quantityUse;
    private LocalDateTime lastTimeUse;
    private Object value;
}
