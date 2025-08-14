package com.book.store.Mapper;

import com.book.store.DTO.BookDTO;
import com.book.store.Entity.Book;
import com.book.store.Entity.Order;
import org.springframework.stereotype.Component;



@Component
public class BookMapper {
    public BookDTO toDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .description(book.getDescription())
                .price(book.getPrice())
                .quantity(book.getQuantity())
                .build();
    }


}
