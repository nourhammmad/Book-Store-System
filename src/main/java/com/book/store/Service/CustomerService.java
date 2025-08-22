package com.book.store.Service;

import com.book.store.Entity.Customer;
import com.book.store.Entity.User;
import com.book.store.Mapper.CustomerMapper;

import com.book.store.Repository.CustomerRepository;
import com.book.store.Repository.OrderRepository;
import com.book.store.Repository.UserRepository;
import com.book.store.server.dto.CustomerApiDto;
import com.book.store.server.dto.CustomerReferenceApiDto;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

private final UserRepository userRepository;

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    // Create customer from API DTO
    public Customer createCustomer(Customer customer) {

        if (customer == null) {
            throw new NullPointerException("Customer must not be null");
        }

        return customerRepository.save(customer);
    }
    // Delete customer
    @Transactional
    public void deleteCustomer(Long id) {
//        if (id == null) {
//            throw new IllegalArgumentException("id is null");
//        }
        customerRepository.deleteById(id);
    }
    // List all customers
    public List<CustomerApiDto> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable).stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }
    // Find by ID
    public Customer findCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return customer;
    }
}
