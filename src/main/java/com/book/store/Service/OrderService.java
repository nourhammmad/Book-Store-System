package com.book.store.service;

import com.book.store.entity.*;
import com.book.store.mapper.OrderMapper;
import com.book.store.repository.BookRepository;
import com.book.store.repository.CustomerRepository;
import com.book.store.repository.OrderRepository;
import com.book.store.repository.UserRepository;
import com.book.store.server.dto.OrderRequestApiDto;
import com.book.store.server.dto.OrderItemRequestApiDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    /**
     * Place an order from DTO
     */
    public Order placeOrder(OrderRequestApiDto orderApiDto) {
        if (orderApiDto == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        if (orderApiDto.getItems() == null || orderApiDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Fetch customer
        String username = authService.getCurrentUserUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found for user: " + username));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        if (orderApiDto.getItems() != null && !orderApiDto.getItems().isEmpty()) {
            for (OrderItemRequestApiDto itemDto : orderApiDto.getItems()) {
                Book book = bookRepository.findById(itemDto.getBook().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Book not found with id " + itemDto.getBook().getId()));

                int quantity = itemDto.getQuantity();
                if (book.getQuantity() < quantity) {
                    throw new IllegalStateException("Requested quantity exceeds available stock for book " + book.getId());
                }
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than zero");
                }

                // Calculate price for this item
                float itemPrice = book.getPrice() * quantity;

                OrderItem item = orderMapper.toEntity(itemDto);
                item.setBook(book);
                item.setPrice(itemPrice);

                order.addItem(item);

                // Decrement stock
                book.setQuantity(book.getQuantity() - quantity);
                bookRepository.save(book);
            }
        }

        // ✅ Balance check
        if (customer.getBalance() < order.getTotalPrice()) {
            throw new IllegalStateException("Insufficient balance. Required: " + order.getTotalPrice() + ", Available: " + customer.getBalance());
        }

        // Deduct balance
        customer.setBalance(customer.getBalance() - order.getTotalPrice());
        customerRepository.save(customer);


        return orderRepository.save(order);
    }

    public List<Order> getPreviousOrders() {
        String username = authService.getCurrentUserUsername();
        Optional<User> user = userRepository.findByUsername(username);
        Long userId = user.map(User::getId).orElse(null);

        return orderRepository.findAllByCustomerId(userId);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }

    /**
     * Delete an order and restore stock
     */
    public void deleteById(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        Order order = findById(orderId);

        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setQuantity(book.getQuantity() + item.getQuantity());
            bookRepository.save(book);
        }

        orderRepository.delete(order);
    }
}