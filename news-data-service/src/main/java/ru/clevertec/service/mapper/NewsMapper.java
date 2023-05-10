package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.data.entity.News;
import ru.clevertec.service.dto.NewsCreateDto;
import ru.clevertec.service.dto.NewsReadDto;
import ru.clevertec.service.dto.SimpleNewsReadDto;

@Mapper(uses = CommentMapper.class)
public interface NewsMapper {

    NewsReadDto toNewsReadDto(News news);

    SimpleNewsReadDto toSimpleNewsReadDto(News news);

    News toNew(NewsCreateDto newsCreateDto);
}
