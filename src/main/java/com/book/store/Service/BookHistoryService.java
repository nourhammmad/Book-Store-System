package com.book.store.Service;

import com.book.store.Entity.BookHistory;
import com.book.store.Repository.BookHistoryRepository;
import com.book.store.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class BookHistoryService {


    private final BookHistoryRepository bookHistoryRepository;
    private final BookRepository bookRepository;


    public BookHistory logChange(Integer entityId, String field, String oldValue, String newValue, Integer changedBy) {
        BookHistory history = new BookHistory();
      //  log.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setField(field);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedBy(changedBy);
        history.setTimestamp(LocalDateTime.now());
        bookHistoryRepository.save(history);

        return  bookHistoryRepository.save(history);
    }


}
