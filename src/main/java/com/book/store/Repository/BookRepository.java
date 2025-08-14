package com.book.store.Repository;

import com.book.store.Entity.Book;
import com.book.store.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
   Optional<Book>  findByTitle(String title);

    @Query("SELECT b.description FROM Book b WHERE b.id = :id")
   String  getDescriptionById(Long id);
  // Book findById(Long id);

}
