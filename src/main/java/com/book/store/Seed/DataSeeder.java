package com.book.store.Seed;

import com.book.store.Entity.Book;
import com.book.store.Entity.Customer;
import com.book.store.Entity.User;
import com.book.store.Entity.Order;
import com.book.store.Service.BookService;
import com.book.store.Service.CustomerService;
import com.book.store.Service.OrderService;
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

        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("test@test.com");
        customer.setAddress("123 Main St, Springfield");
        customer.setBalance(100.0f);

        customer = customerService.createCustomer(customer);
        System.out.println("Seeded customer: " + customer.getId() + " - " + customer.getName());



        // Create a fake book
        Book book = bookService.createBook(
                "John Doe",
                "Spring Boot in Action",
                10,
                29.99f,
                "A practical guide to Spring Boot."
        );

        // Place an order for 2 copies of the book
        Order order = orderService.placeOrder(customer.getId(), book.getId(), 2);

        System.out.println("Seeded order: " + order.getId() + " for book: " + book.getTitle());
    }
}