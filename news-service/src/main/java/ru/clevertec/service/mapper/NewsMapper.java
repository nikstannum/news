package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.NewsUpdateDto;
import ru.clevertec.client.dto.SimpleNewsReadDto;
import ru.clevertec.client.entity.News;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;

@Mapper(uses = {CommentMapper.class, AuthorMapper.class})
public interface NewsMapper {

    @Mapping(target = "comments", ignore = true)
    News toNew(SimpleNewsReadDto simpleNewsReadDto);

    @Mapping(target = "author", ignore = true)
    ClientSimpleNewsReadDto toClientNewsReadDto(News news);

    @Mapping(target = "userId", ignore = true)
    NewsCreateDto toNewsCreateDto(ClientNewsCreateDto clientNewsCreateDto);

    @Mapping(target = "author", ignore = true)
    ClientNewsReadDto toClientNewsReadDto(NewsReadDto newsReadDto);

    @Mapping(target = "userId", ignore = true)
    NewsUpdateDto toNewsUpdateDto(ClientNewsUpdateDto clientNewsUpdateDto);

}
