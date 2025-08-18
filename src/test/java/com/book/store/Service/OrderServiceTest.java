package com.book.store.Service;

import com.book.store.Entity.Book;
import com.book.store.Entity.Customer;
import com.book.store.Entity.Order;
import com.book.store.Entity.OrderItem;
import com.book.store.Mapper.OrderMapper;
import com.book.store.Repository.BookRepository;
import com.book.store.Repository.CustomerRepository;
import com.book.store.Repository.OrderRepository;
import com.book.store.server.dto.OrderApiDto;
import com.book.store.server.dto.BookApiDto;
import com.book.store.server.dto.CustomerApiDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Book book;
    private Customer customer;
    private OrderApiDto orderApiDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        book.setPrice(25.0f);
        book.setQuantity(10);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setBook(book);
        order.setQuantity(2);
        order.setTotalPrice(50.0f);
        order.setOrderDate(LocalDateTime.now());

        BookApiDto bookApiDto = new BookApiDto();
        bookApiDto.setId(1);

        CustomerApiDto customerApiDto = new CustomerApiDto();
        customerApiDto.setId(1L);

        orderApiDto = new OrderApiDto();
        orderApiDto.setBook(bookApiDto);
        orderApiDto.setCustomer(customerApiDto);
        orderApiDto.setQuantity(2);
    }

    @Test
    void findAllReturnsPagedOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(order.getId(), result.getContent().get(0).getId());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void findAllReturnsEmptyPageWhenNoOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of());

        when(orderRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Order> result = orderService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void placeOrderFromDtoCreatesOrderSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(bookRepository).findById(1);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrderCreatesOrderSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(1L, 1, 2);

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(book, result.getBook());
        assertEquals(2, result.getQuantity());
        assertEquals(50.0f, result.getTotalPrice());
        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrderReducesBookQuantity() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.placeOrder(1L, 1, 2);

        assertEquals(8, book.getQuantity()); // 10 - 2 = 8
        verify(bookRepository).save(book);
    }

    @Test
    void placeOrderThrowsExceptionWhenBookNotFound() {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.placeOrder(1L, 999, 2)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository).findById(999);
        verify(customerRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenCustomerNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> orderService.placeOrder(999L, 1, 2)
        );

        assertEquals("Customer not found with id 999", exception.getMessage());
        verify(bookRepository).findById(1);
        verify(customerRepository).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenInsufficientStock() {
        book.setQuantity(1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> orderService.placeOrder(1L, 1, 5)
        );

        assertEquals("Requested quantity exceeds available stock", exception.getMessage());
        verify(bookRepository).findById(1);
        verify(customerRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderHandlesExactStockQuantity() {
        book.setQuantity(2);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(1L, 1, 2);

        assertNotNull(result);
        assertEquals(0, book.getQuantity());
        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteByIdDeletesOrderWhenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        orderService.deleteById(1L);

        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteByIdThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.deleteById(999L)
        );

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void deleteOrderDeletesSuccessfully() {
        order.setId(1L);
        doNothing().when(orderRepository).delete(order);

        orderService.delete(order);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrderThrowsExceptionWhenIdIsNull() {
        order.setId(null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.delete(order)
        );

        assertEquals("Order ID must not be null", exception.getMessage());
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void findByIdReturnsOrderWhenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(customer, result.getCustomer());
        verify(orderRepository).findById(1L);
    }

    @Test
    void findByIdThrowsExceptionWhenNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.findById(999L)
        );

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository).findById(999L);
    }

    @Test
    void placeOrderWithZeroQuantity() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
            () -> orderService.placeOrder(1L, 1, 0)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(bookRepository).findById(1);
        verify(customerRepository).findById(1L);
    }

    @Test
    void placeOrderWithNegativeQuantity() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
            () -> orderService.placeOrder(1L, 1, -5)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(bookRepository).findById(1);
        verify(customerRepository).findById(1L);
    }

    @Test
    void placeOrderCalculatesTotalPriceCorrectly() {
        book.setPrice(15.50f);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            return savedOrder;
        });

        Order result = orderService.placeOrder(1L, 1, 3);

        assertNotNull(result);
        assertEquals(46.50f, result.getTotalPrice());
        assertEquals(3, result.getQuantity());
    }

    @Test
    void placeOrderFromDtoWithInvalidBookId() {
        orderApiDto.getBook().setId(999);
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository).findById(999);
    }

    @Test
    void placeOrderFromDtoWithInvalidCustomerId() {
        orderApiDto.getCustomer().setId(999L);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Customer not found with id 999", exception.getMessage());
    }

    @Test
    void findAllWithNegativePageNumber() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.findAll(-1, 10)
        );

        assertEquals("Page index must not be less than zero", exception.getMessage());
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findAllWithZeroPageSize() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.findAll(0, 0)
        );

        assertEquals("Page size must not be less than one", exception.getMessage());
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void deleteOrderRepositoryThrowsException() {
        order.setId(1L);
        doThrow(new RuntimeException("Database error")).when(orderRepository).delete(order);

        assertThrows(RuntimeException.class, () -> {
            orderService.delete(order);
        });

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteByIdRepositoryThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new RuntimeException("Database error")).when(orderRepository).delete(order);

        assertThrows(RuntimeException.class, () -> {
            orderService.deleteById(1L);
        });

        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(order);
    }

    @Test
    void placeOrderBookRepositorySaveThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(1L, 1, 2);
        });

        verify(bookRepository).save(book);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderOrderRepositorySaveThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(1L, 1, 2);
        });

        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }
}
