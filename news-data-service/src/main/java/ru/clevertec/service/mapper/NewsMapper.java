package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.data.entity.News;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

@Mapper(uses = CommentMapper.class)
public interface NewsMapper {

    @Mapping(target = "comments", source = "comments", ignore = true)
    NewsReadDto toNewsReadDto(News news);

    SimpleNewsReadDto toSimpleNewsReadDto(News news);

    @Mappings({@Mapping(target = "comments", ignore = true),
    @Mapping(target = "id", ignore = true),
    @Mapping(target = "time", ignore = true)})
    News toNews(NewsCreateDto newsCreateDto);

}
