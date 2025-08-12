package com.book.store.Book.Store.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String address;
    private float balance;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<Order> orders;

}
