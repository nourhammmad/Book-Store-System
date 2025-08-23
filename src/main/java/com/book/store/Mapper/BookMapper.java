package com.book.store.mapper;

import com.book.store.entity.Book;
import com.book.store.server.dto.BookApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BookMapper {
    // Map DTO -> Entity
    Book toEntity(BookApiDto dto);

    // Map Entity -> DTO, omit ID in response
    BookApiDto toDto(Book book);

    // Partial update (ignore null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Book partialUpdate(BookApiDto dto, @MappingTarget Book book);
}
