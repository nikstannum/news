package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.client.news.News;
import ru.clevertec.service.dto.NewsCreateUpdateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

@Mapper
public interface NewsMapper {

    @Mapping(source = "time", target = "createTime")
    NewsReadDto toDto(News news);

    @Mappings({@Mapping(source = "userId", target = "authorId"),
            @Mapping(source = "time", target = "createTime")})
    SimpleNewsReadDto toSimpleNewsReadDto(News news);

    News toNews(NewsCreateUpdateDto newsCreateDto);
}
