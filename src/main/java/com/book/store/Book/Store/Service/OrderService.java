package com.book.store.Book.Store.Service;

import com.book.store.Book.Store.Entity.Book;
import com.book.store.Book.Store.Entity.Customer;
import com.book.store.Book.Store.Entity.Order;
import com.book.store.Book.Store.Entity.OrderItem;
import com.book.store.Book.Store.Repository.BookRepository;
import com.book.store.Book.Store.Repository.CustomerRepository;
import com.book.store.Book.Store.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;


    // Get all orders with pagination
    public Page<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }


    // Delete an order
    public void delete(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Order ID must not be null for deletion");
        }
        orderRepository.delete(order);
    }

    // Place a new order for a book
    public Order placeOrder(UUID customerId, UUID bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

        if (book.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(book.getPrice() * quantity);

        Order order = new Order();
        order.setCustomer(customer); // Set the customer on the order
        order.setItems(List.of(orderItem));
        order.setTotalPrice(orderItem.getPrice() * orderItem.getQuantity());
        orderItem.setOrder(order);

        // Reduce stock
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);

        return orderRepository.save(order);
    }

    // Find order by ID
    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
