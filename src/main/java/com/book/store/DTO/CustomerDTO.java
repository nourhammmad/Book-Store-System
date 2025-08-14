package com.book.store.DTO;

import lombok.Data;

@Data
public class CustomerDTO{
    private String name;
    private String email;
    private String address;
    private float balance;
}
