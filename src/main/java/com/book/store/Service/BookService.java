package com.book.store.Service;

import com.book.store.Entity.Book;
import com.book.store.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository  bookRepository;




    public Page<Book>  getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
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





    public Book updateBook( Book updatedBook, Long id) {
        return bookRepository.findById(id)
                .map(book -> {

                    if (updatedBook.getTitle()!=null) book.setTitle(updatedBook.getTitle());
                    if(updatedBook.getPrice()!= 0.0) book.setPrice(updatedBook.getPrice());
                    if (updatedBook.getAuthor()!=null) book.setAuthor(updatedBook.getAuthor());
                    if (updatedBook.getQuantity() !=0) book.setQuantity(updatedBook.getQuantity());
                    if (updatedBook.getDescription()!= null) book.setDescription(updatedBook.getDescription());

                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));


    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public String GetDescriptionById(Long id){
        return bookRepository.getDescriptionById(id);
    }
}

