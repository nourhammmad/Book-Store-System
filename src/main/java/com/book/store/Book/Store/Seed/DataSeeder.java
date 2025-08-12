package com.book.store.Book.Store.Seed;

import com.book.store.Book.Store.Entity.Book;
import com.book.store.Book.Store.Entity.Customer;
import com.book.store.Book.Store.Entity.Order;
import com.book.store.Book.Store.Service.BookService;
import com.book.store.Book.Store.Service.CustomerService;
import com.book.store.Book.Store.Service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final BookService bookService;
    private final OrderService orderService;
    private final CustomerService customerService;

    @PostConstruct
    public void seed() {
        // Create a fake customer
        Customer user = new Customer();
        user.setName("Jane Doe");
        user.setEmail("test@test.com");
        user.setAddress("123 Main St, Springfield");
        user.setBalance(100.0f);
        user = customerService.createCustomer(user);
        System.out.println("Seeded customer: " + user.getId() + " - " + user.getName());


        // Create a fake book
        Book book = bookService.createBook(
                "John Doe",
                "Spring Boot in Action",
                10,
                29.99f,
                "A practical guide to Spring Boot."
        );

        // Place an order for 2 copies of the book
        Order order = orderService.placeOrder(user.getId(), book.getId(), 2);

        System.out.println("Seeded order: " + order.getId() + " for book: " + book.getTitle());
    }
}