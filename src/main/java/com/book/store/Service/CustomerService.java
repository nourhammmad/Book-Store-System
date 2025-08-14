package com.book.store.Service;

import com.book.store.Entity.Customer;
import com.book.store.Mapper.CustomerMapper;
import com.book.store.Repository.CustomerRepository;
import com.book.store.server.dto.CustomerApiDto;
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
        return customerRepository.save(customer);
    }

    // Delete customer
    @Transactional
    public void deleteCustomer(Long id) {
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
    public CustomerApiDto findCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return customerMapper.toDTO(customer);
    }
}
