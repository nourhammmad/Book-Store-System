package com.book.store.Repository;

import com.book.store.Entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
   Optional<Book>  findByTitle(String title);



    @Query("SELECT b.description FROM Book b WHERE b.id = :id")
   String  getDescriptionById(Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findById(Integer id);


}
