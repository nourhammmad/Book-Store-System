package com.book.store.Book.Store.Service;

import com.book.store.Book.Store.Entity.Book;
import com.book.store.Book.Store.Entity.Orders;
import com.book.store.Book.Store.Repository.BookRepository;
import com.book.store.Book.Store.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;


    // Get all orders with pagination
    public Page<Orders> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }


    // Delete an order
    public void delete(Orders order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Order ID must not be null for deletion");
        }
        orderRepository.delete(order);
    }

    // Place a new order for a book
    public Orders placeOrder(UUID bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        Orders order = new Orders();
        order.setBook(book);
        order.setQuantity(quantity);
        order.setTotalPrice(book.getPrice() * quantity);
        order.setOrderDate(LocalDateTime.now());

        // Reduce stock
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);

        return orderRepository.save(order);
    }

    // Find order by ID
    public Orders findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
