package com.book.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Setter
@Getter
public class Customer extends User{



    private String address;
    private float balance;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

}
