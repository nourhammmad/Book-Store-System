package com.book.store.Service;

import com.book.store.Entity.*;
import com.book.store.Mapper.OrderMapper;
import com.book.store.Repository.BookRepository;

import com.book.store.Repository.CustomerRepository;
import com.book.store.Repository.OrderRepository;

import com.book.store.Repository.UserRepository;
import com.book.store.server.dto.OrderApiDto; // your OpenAPI DTO
import com.book.store.server.dto.OrderItemApiDto;

import com.book.store.server.dto.UpdateOrderRequestApiDto;

import jakarta.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final AuthServiceImpl authentication;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    /**
     * Place an order from DTO
     */
    public Order placeOrder(OrderApiDto orderApiDto) {
        // Fetch customer
        Long customerId = orderApiDto.getCustomer().getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id " + customerId));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        if (orderApiDto.getItems() != null && !orderApiDto.getItems().isEmpty()) {
            for (OrderItemApiDto itemDto : orderApiDto.getItems()) {
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

    public List<Order> GetPreviousOrders() {
        String username = authentication.getCurrentUserUsername();
        Optional<User> user = userRepository.findByUsername(username);
        Long userId = user.map(User::getId).orElse(null);

        return  orderRepository.findAllByCustomerId(userId);
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
            throw new IllegalArgumentException("id is null");
        }
        Order order = findById(orderId);

        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setQuantity(book.getQuantity() + item.getQuantity());
            bookRepository.save(book);
        }

        orderRepository.delete(order);
    }

    /**
     * Update order: status and items
     */
//    public Order updateOrder(Long orderId, UpdateOrderRequestApiDto updateDto) {
//        Order order = findById(orderId);
//
//        // Update status
//        if (updateDto.getStatus() != null) {
//            order.setStatus(orderMapper.map(updateDto.getStatus()));
//        }
//
//        // Update items if provided
//        if (updateDto.getItems() != null && !updateDto.getItems().isEmpty()) {
//            // Restore previous stock first
//            for (OrderItem item : order.getItems()) {
//                Book book = item.getBook();
//                book.setQuantity(book.getQuantity() + item.getQuantity());
//                bookRepository.save(book);
//            }
//
//            // Clear old items
//            order.getItems().clear();
//
//            // Add new items
//            for (OrderItemApiDto itemDto : updateDto.getItems()) {
//                Book book = bookRepository.findById(itemDto.getBook().getId())
//                        .orElseThrow(() -> new IllegalArgumentException("Book not found with id " + itemDto.getBook().getId()));
//
//                int quantity = itemDto.getQuantity();
//                if (book.getQuantity() < quantity) {
//                    throw new IllegalStateException("Requested quantity exceeds available stock for book " + book.getId());
//                }
//
//                OrderItem newItem = orderMapper.toEntity(itemDto);
//                newItem.setBook(book);
//                newItem.setPrice(book.getPrice() * quantity);
//                order.addItem(newItem);
//
//                book.setQuantity(book.getQuantity() - quantity);
//                bookRepository.save(book);
//            }
//        }
//
//        return orderRepository.save(order);
//    }
}