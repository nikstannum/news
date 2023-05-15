package ru.clevertec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.clevertec.util.cache.Cache;
import ru.clevertec.util.cache.CacheAdvice;
import ru.clevertec.util.cache.impl.LFUCacheImpl;
import ru.clevertec.util.cache.impl.LRUCacheImpl;

@Configuration
public class AppConfig {
    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnProperty(name = "app.cache.enable", havingValue = "true")
    public Cache cache() {
        String cacheType = environment.getProperty("app.cache.type");
        if ("LFU".equals(cacheType)) {
            return new LFUCacheImpl();
        } else if ("LRU".equals(cacheType)) {
            return new LRUCacheImpl();
        } else {
            throw new IllegalArgumentException("Unknown cache type: " + cacheType);
        }
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.enable", havingValue = "true")
    public CacheAdvice cacheAdvice(Cache cache) {
        return new CacheAdvice(cache);
    }
}
