package com.book.store.Controller;

import com.book.store.Entity.Book;

import com.book.store.Mapper.BookMapper;
import com.book.store.Service.BookService;

import com.book.store.server.dto.BookApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {


    private final BookService bookService;
    private final BookMapper bookMapper;


    @GetMapping
    public ResponseEntity<Page<BookApiDto>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Book> books = bookService.getAllBooks(page, size);
        Page<BookApiDto> bookDTOS = books.map(bookMapper::toDto);
        return ResponseEntity.ok(bookDTOS);
    }


    @PostMapping("/addbook")
    public ResponseEntity<BookApiDto> AddBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int quantity,@RequestParam float price,
            @RequestParam String description) {
        Book book = bookService.createBook(author, title, quantity, price, description);
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookApiDto> GetBookById(@PathVariable Long id){
        Book book = bookService.getBookById(id);
        return  ResponseEntity.ok(bookMapper.toDto(book));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookApiDto> UpdateBook(@RequestBody Book UpdatedBook, @PathVariable Long id){
        Book book = bookService.updateBook(UpdatedBook, id);
        return ResponseEntity.ok(bookMapper.toDto(book));
    }

    @DeleteMapping("/delete/{id}")
    public  ResponseEntity<BookApiDto> DeleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/description/{id}")
    public  ResponseEntity<String> GetDescriptionById(@PathVariable Long id){
        String description = bookService.GetDescriptionById(id);
        return ResponseEntity.ok(description.toLowerCase());

    }
}
