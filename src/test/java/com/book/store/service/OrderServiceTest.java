package com.book.store.service;

import com.book.store.entity.*;
import com.book.store.mapper.OrderMapper;
import com.book.store.repository.BookRepository;
import com.book.store.repository.CustomerRepository;
import com.book.store.repository.OrderRepository;
import com.book.store.repository.UserRepository;
import com.book.store.server.dto.*;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.ArrayList;
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
    private UserRepository userRepository;

    @Mock
    private AuthServiceImpl authentication;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Book book;
    private Customer customer;
    private User user;
    private OrderRequestApiDto orderRequestApiDto;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(25.0f);
        book.setQuantity(10);

        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("John Doe");
        customer.setEmail("john@example.com");
        customer.setBalance(100.0f);

        user = new Customer();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(book);
        orderItem.setQuantity(2);
        orderItem.setPrice(50.0f);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.addItem(orderItem);

        BookReferenceApiDto bookApiDto = new BookReferenceApiDto();
        bookApiDto.setId(1L);

        OrderItemRequestApiDto orderItemRequestApiDto = new OrderItemRequestApiDto();
        orderItemRequestApiDto.setBook(bookApiDto);
        orderItemRequestApiDto.setQuantity(2);

        orderRequestApiDto = new OrderRequestApiDto();
        orderRequestApiDto.setItems(new ArrayList<>(List.of(orderItemRequestApiDto)));
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
    void placeOrderThrowsExceptionWhenUserNotFound() {
        when(authentication.getCurrentUserUsername()).thenReturn("unknown");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("User not found: unknown", exception.getMessage());
        verify(authentication).getCurrentUserUsername();
        verify(userRepository).findByUsername("unknown");
        verify(customerRepository, never()).findById(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenCustomerNotFound() {
        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Customer not found for user: johndoe", exception.getMessage());
        verify(authentication).getCurrentUserUsername();
        verify(userRepository).findByUsername("johndoe");
        verify(customerRepository).findById(1L);
    }

    @Test
    void placeOrderThrowsExceptionWhenBookNotFound() {
        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Book not found with id 1", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenInsufficientStock() {
        book.setQuantity(1);
        orderRequestApiDto.getItems().get(0).setQuantity(5);

        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Requested quantity exceeds available stock for book 1", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenInsufficientBalance() {
        customer.setBalance(10.0f); // Not enough for 50.0f order

        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemRequestApiDto.class))).thenReturn(orderItem);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertTrue(exception.getMessage().contains("Insufficient balance"));
        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenZeroQuantity() {
        orderRequestApiDto.getItems().get(0).setQuantity(0);

        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsExceptionWhenNegativeQuantity() {
        orderRequestApiDto.getItems().get(0).setQuantity(-5);

        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderWithEmptyItemsList() {
        orderRequestApiDto.setItems(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Order must contain at least one item", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderWithNullItemsList() {
        orderRequestApiDto.setItems(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(orderRequestApiDto)
        );

        assertEquals("Order must contain at least one item", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteByIdDeletesOrderAndRestoresStock() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        orderService.deleteById(1L);

        assertEquals(12, book.getQuantity()); // 10 + 2 = 12 (restored stock)
        verify(orderRepository).findById(1L);
        verify(bookRepository).save(book);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteByIdThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.deleteById(999L)
        );

        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void findByIdReturnsOrderWhenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(customer, result.getCustomer());
        assertFalse(result.getItems().isEmpty());
        verify(orderRepository).findById(1L);
    }

    @Test
    void findByIdThrowsExceptionWhenNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.findById(999L)
        );

        assertEquals("Order not found with id 999", exception.getMessage());
        verify(orderRepository).findById(999L);
    }

    @Test
    void getPreviousOrdersReturnsUserOrders() {
        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(orderRepository.findAllByCustomerId(1L)).thenReturn(List.of(order));

        List<Order> result = orderService.getPreviousOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        verify(authentication).getCurrentUserUsername();
        verify(userRepository).findByUsername("johndoe");
        verify(orderRepository).findAllByCustomerId(1L);
    }

    @Test
    void getPreviousOrdersReturnsEmptyListWhenNoOrders() {
        when(authentication.getCurrentUserUsername()).thenReturn("johndoe");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(orderRepository.findAllByCustomerId(1L)).thenReturn(List.of());

        List<Order> result = orderService.getPreviousOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(authentication).getCurrentUserUsername();
        verify(userRepository).findByUsername("johndoe");
        verify(orderRepository).findAllByCustomerId(1L);
    }

    @Test
    void deleteByIdWithMultipleItemsRestoresAllStock() {
        // Add second item to order
        Book book2 = new Book();
        book2.setId(2L);
        book2.setQuantity(15);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setBook(book2);
        orderItem2.setQuantity(3);

        order.addItem(orderItem2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        orderService.deleteById(1L);

        assertEquals(12, book.getQuantity()); // 10 + 2 = 12
        assertEquals(18, book2.getQuantity()); // 15 + 3 = 18
        verify(orderRepository).findById(1L);
        verify(bookRepository, times(2)).save(any(Book.class));
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void findAllWithNegativePageNumber() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.findAll(-1, 10)
        );

        assertTrue(exception.getMessage().contains("Page index must not be less than zero") ||
                exception.getMessage().contains("must not be negative"));
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findAllWithZeroPageSize() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.findAll(0, 0)
        );

        assertTrue(exception.getMessage().contains("Page size must not be less than one") ||
                exception.getMessage().contains("must be greater than"));
        verify(orderRepository, never()).findAll(any(Pageable.class));
    }
}