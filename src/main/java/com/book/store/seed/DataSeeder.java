package com.book.store.seed;
import com.book.store.entity.Admin;
import com.book.store.entity.Customer;
import com.book.store.entity.Order;
import com.book.store.repository.AdminRepository;
import com.book.store.repository.UserRepository;

import com.book.store.entity.Book;
import com.book.store.security.CustomUserDetails;
import com.book.store.server.dto.CustomerReferenceApiDto;
import com.book.store.server.dto.BookReferenceApiDto;
import com.book.store.server.dto.OrderRequestApiDto;
import com.book.store.server.dto.OrderItemRequestApiDto;
import com.book.store.service.BookService;
import com.book.store.service.CustomerService;
import com.book.store.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final BookService bookService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OrderService orderService;


    @PostConstruct
    public void seed() {
        // Create customer DTO
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        customer.setEmail("john@email.com");
        customer.setAddress("123 Main St, Anytown, USA");
        customer.setBalance(500f);
        customer.setPassword(passwordEncoder.encode("password"));
        Customer savedCustomer = customerService.createCustomer(customer);
        System.out.println("👤 Seeded customer: " + savedCustomer.getUsername());

        // Set up authentication context for the seeded customer
        CustomUserDetails userDetails = new CustomUserDetails(savedCustomer);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Books to seed
        List<Book> books = Arrays.asList(
                createBook("9781234567890", "Author One", "Spring Boot in Action", "N/A", 1, 29f, ""),
                createBook("9781234567891", "Author Two", "Java Concurrency Mastery", "N/A", 2, 35f, ""),
                createBook("9781234567892", "Author Three", "Microservices with Spring Cloud", "N/A", 1, 40.4f, ""),
                createBook("9781234567893", "Author Four", "Clean Code", "N/A", 1, 25f, ""),
                createBook("9781234567894", "Author Five", "Effective Java", "N/A", 1, 33f, "")
        );

        // Save books and place orders
        for (Book book : books) {
            Book savedBook = bookService.createBook(book);
            System.out.println("📚 Seeded book: " + savedBook.getTitle());

            OrderRequestApiDto orderRequest = getOrderRequestApiDto(savedCustomer, savedBook);

            Order order = orderService.placeOrder(orderRequest);
            System.out.println("🛒 Placed order for: " + book.getTitle() + " | Order ID: " + order.getId());
        }

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setEmail("admin@email.com");
        userRepository.save(admin);
        adminRepository.save(admin);
        System.out.println("👤 Seeded admin user: " + admin.getUsername());
        SecurityContextHolder.clearContext();

    }

    private static OrderRequestApiDto getOrderRequestApiDto(Customer savedCustomer, Book savedBook) {
        CustomerReferenceApiDto customerRef = new CustomerReferenceApiDto(
            savedCustomer.getId(),
            savedCustomer.getUsername()
        );

        BookReferenceApiDto bookRef = new BookReferenceApiDto(savedBook.getId());

        OrderItemRequestApiDto orderItem = new OrderItemRequestApiDto(bookRef, 1);

        return new OrderRequestApiDto(
                List.of(orderItem)
        );
    }
    
    private static Book createBook(String isbn, String author, String title, String description, int quantity, float price, String coverImageUrl) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setTitle(title);
        book.setDescription(description);
        book.setQuantity(quantity);
        book.setPrice(price);
        book.setCoverImageUrl(coverImageUrl);
        return book;
    }
}