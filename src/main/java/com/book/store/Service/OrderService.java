package com.book.store.Service;

import com.book.store.Entity.Book;
import com.book.store.Entity.Customer;
import com.book.store.Entity.Order;
import com.book.store.Entity.OrderItem;
import com.book.store.Repository.BookRepository;
import com.book.store.Repository.CustomerRepository;
import com.book.store.Repository.OrderRepository;
import com.examle.demo.server.dto.OrderApiDto; // your OpenAPI DTO
import com.book.store.Mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper; // map entity <-> DTO

    // Find all orders with pagination
    public Page<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }

    // Place an order from OpenAPI DTO
    public Order placeOrder(OrderApiDto orderDto) {
        Long customerId = Long.valueOf(orderDto.getCustomer().getId());
        Long bookId = Long.valueOf(orderDto.getBook().getId());
        int quantity = orderDto.getQuantity();
        return placeOrder(customerId, bookId, quantity); // reuse existing method
    }

    // Delete order by ID
    public void deleteById(Long id) {
        Order order = findById(id);
        delete(order);
    }

    // Delete order by entity
    public void delete(Order order) {
        if (order.getId() == null) throw new IllegalArgumentException("Order ID must not be null");
        orderRepository.delete(order);
    }

    // Existing placeOrder method
    public Order placeOrder(Long customerId, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
        if (book.getQuantity() < quantity)
            throw new RuntimeException("Requested quantity exceeds available stock");

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(book.getPrice() * quantity);

        Order order = new Order();
        order.setCustomer(customer);
        order.setBook(orderItem.getBook());
        order.setItems(List.of(orderItem));
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(orderItem.getPrice());
        order.setQuantity(orderItem.getQuantity());
        orderItem.setOrder(order);

        // Reduce stock
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);

        return orderRepository.save(order);
    }

    // Find order by ID
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
