package com.book.store.Entity;

import jakarta.persistence.*;
import lombok.Data;
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
