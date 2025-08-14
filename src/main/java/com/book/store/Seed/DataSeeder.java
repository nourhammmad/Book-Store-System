package com.book.store.Seed;

import com.book.store.Entity.Book;
import com.book.store.Entity.Customer;
import com.book.store.Entity.Order;
import com.book.store.Service.BookService;
import com.book.store.Service.CustomerService;
import com.book.store.Service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final BookService bookService;
    private final OrderService orderService;
    private final CustomerService customerService;

    @PostConstruct
    public void seed() {
<<<<<<< Updated upstream
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
=======
        // Create customer
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("test@test.com");
        customer.setAddress("123 Main St, Springfield");
        customer.setBalance(500.0f);
        customer = customerService.createCustomer(customer);
        System.out.println("✅ Seeded customer: " + customer.getId());

        // List of books to seed
        List<Book> books = Arrays.asList(
                new Book(null, "Author One", "Spring Boot in Action", "N/A", 29, 20),
                new Book(null, "Author Two", "Java Concurrency Mastery", "N/A", 35, 30),
                new Book(null, "Author Three", "Microservices with Spring Cloud", "N/A", 40, 40.4f),
                new Book(null, "Author Four", "Clean Code", "N/A", 25, 90),
                new Book(null, "Author Five", "Effective Java", "N/A", 33, 88)
        );

        // Save books and place sample orders
        for (Book book : books) {
            Book savedBook = bookService.createBook(
                    book.getAuthor(),
                    book.getTitle(),
                    book.getQuantity(),
                    book.getPrice(),
                    book.getDescription()
            );
            System.out.println("📚 Seeded book: " + savedBook.getTitle());
>>>>>>> Stashed changes

            // Place an order for 1 copy of each book
            Order order = orderService.placeOrder(customer.getId(), savedBook.getId(), 1);
            System.out.println("🛒 Placed order for: " + savedBook.getTitle());
        }
    }
}
