package ru.clevertec.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.clevertec.client.dto.NewsCreateDto;
import ru.clevertec.client.dto.NewsReadDto;
import ru.clevertec.client.dto.NewsUpdateDto;
import ru.clevertec.client.dto.SimpleNewsReadDto;
import ru.clevertec.client.entity.News;
import ru.clevertec.service.dto.ClientNewsCreateDto;
import ru.clevertec.service.dto.ClientNewsReadDto;
import ru.clevertec.service.dto.ClientNewsUpdateDto;
import ru.clevertec.service.dto.ClientSimpleNewsReadDto;

@Mapper
public interface NewsMapper {

    News toNews(SimpleNewsReadDto simpleNewsReadDto);

    ClientSimpleNewsReadDto toClientNewsReadDto(News news);

    NewsCreateDto toNewsCreateDto (ClientNewsCreateDto clientNewsCreateDto);

    ClientNewsReadDto toClientNewsReadDto(NewsReadDto newsReadDto);

    NewsUpdateDto toNewsUpdateDto(ClientNewsUpdateDto clientNewsUpdateDto);

}
