package com.book.store.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
// Using 'orders' as table name to avoid conflict with SQL reserved keywords
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< Updated upstream
=======
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
>>>>>>> Stashed changes
    private LocalDateTime orderDate;
    @ManyToOne
    private Book book;  // <-- This must match .getBook()

    private int quantity; // <-- This must match .getQuantity()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private float totalPrice;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}
