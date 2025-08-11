package com.book.store.Book.Store.Repository;

import com.book.store.Book.Store.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Book findByTitle(String title);
}
