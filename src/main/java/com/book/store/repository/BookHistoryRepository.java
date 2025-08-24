package com.book.store.repository;

import com.book.store.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookHistoryRepository  extends JpaRepository<BookHistory, Long> {
}
