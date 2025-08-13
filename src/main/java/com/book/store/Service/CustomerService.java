package com.book.store.Service;

import com.book.store.Entity.Customer;
import com.book.store.Entity.Customer;
import com.book.store.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {


    private final CustomerRepository customerRepository;
    private final OrderService orderService; // Assuming OrderService is available to handle order placement


    //TODO: make dto and verify later
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }
    public Customer updateCustomer(UUID id, Customer updatedCustomer) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setName(updatedCustomer.getName());
                    customer.setEmail(updatedCustomer.getEmail());
                    return customerRepository.save(customer);
                })
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }
    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email " + email));
    }
    public List<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll();
    }
}
