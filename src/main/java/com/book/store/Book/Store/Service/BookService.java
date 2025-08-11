package com.book.store.Book.Store.Service;

import com.book.store.Book.Store.Entity.Book;
import com.book.store.Book.Store.Entity.Orders;
import com.book.store.Book.Store.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository  bookRepository;




    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(UUID id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(UUID id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

    public String GetDescriptionById(UUID Id){
       Optional<Book> book= bookRepository.findById(Id);
       return book.map(Book::getDescription).orElse(null);
    }
}
