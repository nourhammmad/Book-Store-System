package com.book.store.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Customer extends User {
    private float balance;
    private String address;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<Order> orders;
}
