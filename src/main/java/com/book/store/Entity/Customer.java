package com.book.store.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String address;
    private float balance;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<Order> orders;

}
