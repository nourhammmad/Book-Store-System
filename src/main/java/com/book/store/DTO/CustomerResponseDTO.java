package com.book.store.DTO;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private float balance;
}
