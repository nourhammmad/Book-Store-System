package com.book.store.Service;

import com.book.store.Entity.Book;
import com.book.store.Mapper.BookMapper;
import com.book.store.Repository.BookRepository;
import com.book.store.exception.response.ErrorDetails;
import com.book.store.exception.response.ViolationErrors;
import com.book.store.server.dto.BookApiDto;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;


    public List<BookApiDto> getAllBooks(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number must be non-negative and size must be positive");
        }
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Book createBook(Book book) {


        return bookRepository.save(book);
    }


    public Book updateBook(BookApiDto updatedBook, Long id) {
        return bookRepository.findById(id)
                .map(book -> {

                    if (updatedBook.getTitle() != null) book.setTitle(updatedBook.getTitle());
                    if (updatedBook.getPrice() != null && updatedBook.getPrice() != 0.0) {
                        book.setPrice(updatedBook.getPrice());
                    }
                    // if (updatedBook.getAuthor()!=null) book.setAuthor(updatedBook.getAuthor());
                    // if (updatedBook.getQuantity() !=0) book.setQuantity(updatedBook.getQuantity());
                    //if (updatedBook.getDescription()!= null) book.setDescription(updatedBook.getDescription());

                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));


    }

    public void deleteBook(Long id) {

        if (id == null) {
            throw new ValidationException("id is null");
        }
        bookRepository.deleteById(id);
    }

    public String GetDescriptionById(Long id){
        if (id == null) {
            throw new ValidationException("id is null");
        }
        return bookRepository.getDescriptionById(id);
    }

    public Book getBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + isbn));
    }
}
