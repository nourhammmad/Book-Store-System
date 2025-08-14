package com.book.store.Service;

import com.book.store.DTO.CustomerDTO;
import com.book.store.DTO.CustomerResponseDTO;
import com.book.store.Entity.Customer;
import com.book.store.Repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Convert entity to DTO
    private CustomerDTO toDto(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setBalance(customer.getBalance());
        return dto;
    }

    // Convert DTO to entity
    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setBalance(dto.getBalance());
        return customer;
    }

    // Create customer
    // Get by ID


    // Update
    public CustomerResponseDTO createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setBalance(dto.getBalance());
        Customer saved = customerRepository.save(customer);

        // Map to response DTO
        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setAddress(saved.getAddress());
        response.setBalance(saved.getBalance());
        return response;
    }

    // Delete
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // List all
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
