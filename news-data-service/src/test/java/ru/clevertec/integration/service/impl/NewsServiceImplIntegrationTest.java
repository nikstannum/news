package ru.clevertec.integration.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.BaseIntegrationTest;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.impl.NewsServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class NewsServiceImplIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private NewsServiceImpl service;

    @Test
    void findUserByIdShouldReturnNotNull() {
        NewsReadDto actual = service.findById(1L, 1, 2);
        assertThat(actual).isNotNull();
    }
}
