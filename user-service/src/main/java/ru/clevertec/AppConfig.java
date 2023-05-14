package ru.clevertec;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.clevertec.util.cache.Cache;
import ru.clevertec.util.cache.CacheAdvice;
import ru.clevertec.util.cache.impl.LFUCacheImpl;
import ru.clevertec.util.cache.impl.LRUCacheImpl;
import ru.clevertec.web.interceptor.RequestInterceptor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Class with configuration for microservice excluding security management
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {


    private final Environment environment;
    private final RequestInterceptor interceptor;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }

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
