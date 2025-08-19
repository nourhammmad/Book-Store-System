package com.book.store.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class BookHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   // private String entityType;
    private Long entityId;
    private String field;
    private String oldValue;
    private String newValue;


    private Long changedBy;
    private LocalDateTime timestamp;

}
