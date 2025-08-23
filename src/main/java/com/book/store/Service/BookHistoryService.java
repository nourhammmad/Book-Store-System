package com.book.store.service;

import com.book.store.entity.BookHistory;
import com.book.store.repository.BookHistoryRepository;
import com.book.store.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class BookHistoryService {


    private final BookHistoryRepository bookHistoryRepository;
    private final BookRepository bookRepository;


    public BookHistory logChange(Long entityId, String field, String oldValue, String newValue, Long changedBy) {
        BookHistory history = new BookHistory();
      //  log.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setField(field);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedBy(changedBy);
        history.setTimestamp(LocalDateTime.now());


        return  bookHistoryRepository.save(history);
    }


}
