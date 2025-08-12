package com.book.store.Service;

import com.book.store.Entity.Book;
import com.book.store.Repository.BookRepository;
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

    public Book createBook(String author, String title, int quantity, float price, String description) {

        Book book = new Book();
        book.setAuthor(author);
        book.setQuantity(quantity);
        book.setTitle(title);
        book.setDescription(description);
        book.setPrice(price);

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
