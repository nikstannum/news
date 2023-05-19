package ru.clevertec.util.cache.redis;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "REDIS")
public class RedisConfig {

    @Value("${app.cache.redis.time-to-live-news}")
    private Long timeNews;
    @Value("${app.cache.redis.time-to-live-comment}")
    private Long timeComment;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder.withCacheConfiguration("news",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(timeNews)))
                .withCacheConfiguration("comment",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(timeComment)));
    }
}
