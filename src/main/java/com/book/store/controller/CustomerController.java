package com.book.store.controller;

import com.book.store.server.api.CustomersApi;
import com.book.store.server.dto.CustomerApiDto;
import com.book.store.server.dto.PaginatedCustomerResponseApiDto;
import com.book.store.service.CustomerService;
import com.book.store.entity.Customer;
import com.book.store.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;


    @Override
    public ResponseEntity<PaginatedCustomerResponseApiDto> adminCustomerGet(Integer page, Integer size) {
        PaginatedCustomerResponseApiDto customerDtos = customerService.getAllCustomers(page, size);
        return ResponseEntity.ok(customerDtos);
    }

    @Override
    public ResponseEntity<Void> adminCustomerIdDelete(Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CustomerApiDto> adminCustomerIdGet(Long id) {
        Customer customer = customerService.findCustomerById(id);
        CustomerApiDto dto = customerMapper.toDTO(customer);
        return ResponseEntity.ok(dto);
    }
}