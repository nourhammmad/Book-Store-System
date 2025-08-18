package com.book.store.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.book.store.Entity.User;
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


    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime orderDate;
    @ManyToOne
    private Book book;

    private int quantity;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private float totalPrice;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}
