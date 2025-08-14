package com.book.store.Repository;

import com.book.store.Entity.Book;
import com.book.store.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
   Optional<Book>  findByTitle(String title);
   Optional<Book> findById(Long id);

}
