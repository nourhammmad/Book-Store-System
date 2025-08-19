package com.book.store.Repository;

import com.book.store.Entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookHistoryRepository  extends JpaRepository<BookHistory, Long> {
}
