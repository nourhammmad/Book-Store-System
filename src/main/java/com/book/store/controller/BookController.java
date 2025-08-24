package com.book.store.controller;

import com.book.store.entity.Book;
import com.book.store.mapper.BookMapper;
import com.book.store.server.dto.BookCreateRequestApiDto;
import com.book.store.server.dto.PaginatedBookResponseApiDto;
import com.book.store.service.BookService;
import com.book.store.server.api.BooksApi;
import com.book.store.server.dto.BookApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BookController implements BooksApi {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @Override
    public ResponseEntity<BookApiDto> createBook(@Valid BookCreateRequestApiDto bookCreateRequestApiDto) {
        Book book = bookMapper.toEntity(bookCreateRequestApiDto);
        Book savedBook = bookService.createBook(book);
        BookApiDto responseDto = bookMapper.toDto(savedBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Override
    public ResponseEntity<Void> deleteBookById(Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PaginatedBookResponseApiDto> findAllBooks(Integer page, Integer size) {
        PaginatedBookResponseApiDto bookDto = bookService.getAllBooks(page, size);
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookApiDto> findBookById(Long id) {
        Book book = bookService.getBookById(id);
        BookApiDto dto = bookMapper.toDto(book);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<BookApiDto> findBookByIsbn(String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        BookApiDto dto = bookMapper.toDto(book);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/book/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadBookCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            // Verify book exists
            bookService.getBookById(id);

            String coverUrl = bookService.uploadBookCover(file);

            // Update book with cover URL
            Book book = bookService.getBookById(id);
            book.setCoverImageUrl(coverUrl);
            bookService.createBook(book); // This will update since ID exists

            return ResponseEntity.ok(coverUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }
}