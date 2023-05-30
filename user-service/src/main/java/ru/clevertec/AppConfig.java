package ru.clevertec;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.clevertec.util.cache.Cache;
import ru.clevertec.util.cache.CacheAdvice;
import ru.clevertec.util.cache.impl.LFUCacheImpl;
import ru.clevertec.util.cache.impl.LRUCacheImpl;

/**
 * Class with configuration for microservice excluding security management
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {


    private final Environment environment;

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
    public CacheAdvice cacheAdvice(Cache cache, ExpressionParser parser, EvaluationContext context) {
        return new CacheAdvice(cache, parser, context);
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.enable", havingValue = "true")
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.enable", havingValue = "true")
    public EvaluationContext evaluationContext() {
        return SimpleEvaluationContext.forReadOnlyDataBinding().build();
    }
}
