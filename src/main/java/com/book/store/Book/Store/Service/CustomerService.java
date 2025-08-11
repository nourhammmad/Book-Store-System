package com.book.store.Book.Store.Service;

import com.book.store.Book.Store.Entity.Customer;
import com.book.store.Book.Store.Entity.Orders;
import com.book.store.Book.Store.Repository.CustomerRepository;
import jakarta.persistence.criteria.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setName(updatedCustomer.getName());
                    customer.setEmail(updatedCustomer.getEmail());
                    return customerRepository.save(customer);
                })
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email " + email));
    }
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll();
    }
    public Page<Customer> searchCustomersByName(String name, Pageable pageable) {
        return customerRepository.findByNameContaining(name, pageable);
    }
    public Page<Customer> getCustomersByBookTitle(String title, Pageable pageable) {
        return customerRepository.findCustomersByBookTitle(title, pageable);
    }

    public Orders placeOrder(Long customerId, UUID bookId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

        // Assuming OrderService is available to handle order placement
        if(bookId == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid book ID or quantity");
        }
        //  TODO:check balance


        Orders order = orderService.placeOrder(bookId, quantity);
        customer.getOrders().add(order);
        customerRepository.save(customer);

        return order;
    }




}
