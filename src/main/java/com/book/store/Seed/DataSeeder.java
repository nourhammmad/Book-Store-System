package com.book.store.Seed;

import com.book.store.server.dto.CustomerApiDto;

import com.book.store.Entity.Book;
import com.book.store.Entity.Customer;
import com.book.store.Entity.Order;
import com.book.store.Mapper.CustomerMapper;
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
    private final CustomerMapper customerMapper;

    @PostConstruct
    public void seed() {
        // Create customer DTO
        CustomerApiDto customerApiDto = new CustomerApiDto();
        customerApiDto.setName("Jane Doe");
        customerApiDto.setEmail("test@test.com");
        customerApiDto.setAddress("123 Main St, Springfield");
        customerApiDto.setBalance(500.0f);

// Convert API DTO → entity
        Customer savedCustomer = customerService.createCustomer(customerMapper.toEntity(customerApiDto));

        // Books to seed
        List<Book> books = Arrays.asList(
                new Book(null, "Author One", "Spring Boot in Action", "N/A", 20, 29f),
                new Book(null, "Author Two", "Java Concurrency Mastery", "N/A", 30, 35f),
                new Book(null, "Author Three", "Microservices with Spring Cloud", "N/A", 40, 40.4f),
                new Book(null, "Author Four", "Clean Code", "N/A", 90, 25f),
                new Book(null, "Author Five", "Effective Java", "N/A", 88, 33f)
        );

        // Save books and place orders
        for (Book book : books) {
            Book savedBook = bookService.createBook(
                    book.getAuthor(),
                    book.getTitle(),
                    book.getQuantity(),
                    book.getPrice(),
                    book.getDescription()
            );
            System.out.println("📚 Seeded book: " + savedBook.getTitle());

            // Order 1 copy of each
            Order order = orderService.placeOrder(savedCustomer.getId(), savedBook.getId(), 1);
            System.out.println("🛒 Placed order for: " + savedBook.getTitle());
        }
    }
}
