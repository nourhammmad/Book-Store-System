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

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    private String address;
    private float balance;

    @OneToMany(mappedBy = "customer")
    //@JoinColumn(name = "user_id")
    private List<Order> orders;

}
