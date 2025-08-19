//package com.book.store.Controller;
//
//import com.book.store.Entity.Book;
//
//import com.book.store.Mapper.BookMapper;
//import com.book.store.Service.BookService;
//import com.book.store.server.api.BooksApi;
//import com.book.store.server.dto.BookApiDto;
//
//import com.book.store.server.dto.BooksApiDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//public class BookController implements BooksApi {
//
//
//    private final BookService bookService;
//    private final BookMapper bookMapper;
//
//    public BookController(BookMapper bookMapper, BookService bookService) {
//        this.bookMapper = bookMapper;
//        this.bookService = bookService;
//    }
//    @Override
//    public ResponseEntity<BookApiDto> createBook(BookApiDto bookApiDto) {
//        Book book = BookMapper.INSTANCE.toEntity(bookApiDto);
//        Book savedBook = bookService.createBook(book);
//        BookApiDto responseDto =BookMapper.INSTANCE.toDto(savedBook);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @Override
//    public ResponseEntity<Void> deleteBookById(Long id) {
//        bookService.deleteBook(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
//
//    @Override
//    public ResponseEntity<BooksApiDto> findAllBooks(Integer page, Integer size) {
//
//        List<BookApiDto> bookDto = bookService.getAllBooks(page, size);
//        BooksApiDto responseDto = new BooksApiDto();
//        responseDto.setBooks(bookDto);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
//
//    }
//
//    @Override
//    public ResponseEntity<BookApiDto> findBookById(Long id) {
//          return new ResponseEntity<>(BookMapper.INSTANCE.toDto(bookService.getBookById(id)), HttpStatus.OK);
//    }
//
//    @Override
//    public ResponseEntity<BookApiDto> updateBookById(Long id, BookApiDto bookApiDto) {
//        Book book = bookService.updateBook(bookApiDto, id);
//        return ResponseEntity.ok(bookMapper.toDto(book));
//    }
//
//
//}
