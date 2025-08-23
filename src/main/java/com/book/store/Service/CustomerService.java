package com.book.store.service;

import com.book.store.entity.Customer;
import com.book.store.mapper.CustomerMapper;
import com.book.store.repository.CustomerRepository;
import com.book.store.server.dto.CustomerApiDto;
import com.book.store.server.dto.PaginatedCustomerResponseApiDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    // Create customer from API DTO
    public Customer createCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        // Optionally, add more field-level validation here
        return customerRepository.save(customer);
    }

    // Delete customer
    @Transactional
    public void deleteCustomer(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    // List all customers
    public PaginatedCustomerResponseApiDto getAllCustomers(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        Pageable pageable = PageRequest.of(page, size);
        var customerPage = customerRepository.findAll(pageable);
        List<CustomerApiDto> content = customerPage.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
        int totalPages = customerPage.getTotalPages();
        long totalElements = customerPage.getTotalElements();

        PaginatedCustomerResponseApiDto response = new PaginatedCustomerResponseApiDto(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
        response.setFirst(page == 0);
        response.setLast(page == totalPages - 1);

        return response;
    }

    // Find by ID
    public Customer findCustomerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }
}