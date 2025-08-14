package com.book.store.Mapper;
import com.book.store.Entity.Book;
import com.book.store.server.dto.BookApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookApiDto toDto(Book book);

    Book toEntity(BookApiDto bookDTO);
}

