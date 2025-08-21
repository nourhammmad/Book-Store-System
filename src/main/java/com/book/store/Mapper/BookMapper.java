package com.book.store.Mapper;

import com.book.store.Entity.Book;
import com.book.store.server.dto.BookApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    // Map DTO -> Entity
    Book toEntity(BookApiDto dto);

    // Map Entity -> DTO, omit ID in response
    BookApiDto toDto(Book book);

    // Partial update (ignore null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Book partialUpdate(BookApiDto dto, @MappingTarget Book book);
}
