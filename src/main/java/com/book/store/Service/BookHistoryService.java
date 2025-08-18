package com.book.store.Service;

import com.book.store.Entity.BookHistory;
import com.book.store.Repository.BookHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class BookHistoryService {


    private final BookHistoryRepository bookHistoryRepository;



    public BookHistory logChange(Integer entityId, String field, String oldValue, String newValue, Integer changedBy) {
        BookHistory history = new BookHistory();
      //  log.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setFieldChanged(field);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedBy(changedBy);
        history.setTimestamp(LocalDateTime.now());

        return  bookHistoryRepository.save(history);
    }


}
