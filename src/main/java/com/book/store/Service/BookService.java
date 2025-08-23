package com.book.store.service;

import com.book.store.entity.Book;
import com.book.store.mapper.BookMapper;
import com.book.store.repository.BookRepository;
import com.book.store.server.dto.BookApiDto;
import jakarta.persistence.EntityNotFoundException;
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

    public List<BookApiDto> getAllBooks(Integer page, Integer size) {
        if (page == null || page < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (size == null || size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public Book getBookById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    public Book createBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        // Optionally, add more field-level validation here
        return bookRepository.save(book);
    }

    // Uncomment and update if needed
    // public Book updateBook(BookApiDto updatedBook, Long id) {
    //     if (updatedBook == null) {
    //         throw new IllegalArgumentException("Updated book cannot be null");
    //     }
    //     if (id == null) {
    //         throw new IllegalArgumentException("ID cannot be null");
    //     }
    //     return bookRepository.findById(id)
    //             .map(book -> {
    //                 if (updatedBook.getTitle() != null) book.setTitle(updatedBook.getTitle());
    //                 if (updatedBook.getPrice() != null && updatedBook.getPrice() != 0.0) {
    //                     book.setPrice(updatedBook.getPrice());
    //                 }
    //                 // Add more field updates and validation as needed
    //                 return bookRepository.save(book);
    //             })
    //             .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    // }

    public void deleteBook(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    public String getDescriptionById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        String description = bookRepository.getDescriptionById(id);
        if (description == null) {
            throw new EntityNotFoundException("Description not found for book with id: " + id);
        }
        return description;
    }

    public Book getBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ISBN: " + isbn));
    }
}