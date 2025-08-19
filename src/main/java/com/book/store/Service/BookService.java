package com.book.store.Service;

import com.book.store.Entity.Book;
import com.book.store.Mapper.BookMapper;
import com.book.store.Repository.BookRepository;
import com.book.store.server.dto.BookApiDto;
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

    public Book getBookById(Integer id) {
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Book createBook(Book book) {


        return bookRepository.save(book);
    }


    public Book updateBook(BookApiDto updatedBook, Integer id) {
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

    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    public String GetDescriptionById(Integer id) {
        return bookRepository.getDescriptionById(id);
    }
}

