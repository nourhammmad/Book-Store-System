package com.book.store.Seed;
import com.book.store.Entity.Admin;
import com.book.store.Repository.AdminRepository;
import com.book.store.Repository.CustomerRepository;
import com.book.store.Repository.UserRepository;
import com.book.store.server.dto.CustomerApiDto;

import com.book.store.Entity.Book;
import com.book.store.Entity.Customer;
import com.book.store.Mapper.CustomerMapper;
import com.book.store.Service.BookService;
import com.book.store.Service.CustomerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final BookService bookService;
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;


    @PostConstruct
    public void seed() {
        // Create customer DTO
        CustomerApiDto customerApiDto = new CustomerApiDto();
        customerApiDto.setUsername("Jane Doe");
        customerApiDto.setPassword(passwordEncoder.encode("password"));
        customerApiDto.setEmail("test@test.com");
        customerApiDto.setAddress("123 Main St, Springfield");
        customerApiDto.setBalance(500.0f);

// Convert API DTO → entity
        Customer savedCustomer = customerService.createCustomer(customerMapper.toEntity(customerApiDto));


        // Books to seed
        List<Book> books = Arrays.asList(
                new Book(null, "Author One", "Spring Boot in Action", "N/A", 1, 29f),
                new Book(null, "Author Two", "Java Concurrency Mastery", "N/A", 2, 35f),
                new Book(null, "Author Three", "Microservices with Spring Cloud", "N/A", 1, 40.4f),
                new Book(null, "Author Four", "Clean Code", "N/A", 1, 25f),
                new Book(null, "Author Five", "Effective Java", "N/A", 1, 33f)
        );

        // Save books and place orders
        for (Book book : books) {
            Book savedBook = bookService.createBook(book);
            System.out.println("📚 Seeded book: " + savedBook.getTitle());
//
//            // Order 1 copy of each
//            Order order = orderService.placeOrder(savedCustomer.getId(), book.getId(), 1);
//            System.out.println("🛒 Placed order for: " + savedBook.getTitle());
        }

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setEmail("admin@email.com");
        userRepository.save(admin);
        adminRepository.save(admin);
        System.out.println("👤 Seeded admin user: " + admin.getUsername());
    }
}