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
import com.book.store.server.dto.OrderItemApiDto;
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
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Book book;
    private Customer customer;
    private OrderApiDto orderApiDto;
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

        BookApiDto bookApiDto = new BookApiDto();
        bookApiDto.setId(1L);

        CustomerApiDto customerApiDto = new CustomerApiDto();
        customerApiDto.setId(1L);

        OrderItemApiDto orderItemApiDto = new OrderItemApiDto();
        orderItemApiDto.setBook(bookApiDto);
        orderItemApiDto.setQuantity(2);

        orderApiDto = new OrderApiDto();
        orderApiDto.setCustomer(customerApiDto);
        orderApiDto.setItems(new ArrayList<>(List.of(orderItemApiDto))); // Use mutable list
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
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemApiDto.class))).thenReturn(orderItem);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(bookRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrderFromDtoReducesBookQuantity() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemApiDto.class))).thenReturn(orderItem);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.placeOrder(orderApiDto);

        assertEquals(8, book.getQuantity()); // 10 - 2 = 8
        verify(bookRepository).save(book);
    }

    @Test
    void placeOrderFromDtoThrowsExceptionWhenBookNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Book not found with id 1", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderFromDtoThrowsExceptionWhenCustomerNotFound() {
        orderApiDto.getCustomer().setId(999L);
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Customer not found with id 999", exception.getMessage());
        verify(customerRepository).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderFromDtoThrowsExceptionWhenInsufficientStock() {
        book.setQuantity(1);
        orderApiDto.getItems().get(0).setQuantity(5);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Requested quantity exceeds available stock for book 1", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(customerRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderFromDtoHandlesExactStockQuantity() {
        book.setQuantity(2);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemApiDto.class))).thenReturn(orderItem);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        assertEquals(0, book.getQuantity());
        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteByIdDeletesOrderAndRestoresStock() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        doNothing().when(orderRepository).delete(order);

        orderService.deleteById(1L);

        assertEquals(12, book.getQuantity()); // 10 + 2 = 12 (restored stock)
        verify(orderRepository).findById(1L);
        verify(bookRepository).save(book);
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteByIdThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.deleteById(999L)
        );

        assertEquals("Order not found with id 999", exception.getMessage());
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
    void placeOrderFromDtoWithZeroQuantity() {
        orderApiDto.getItems().get(0).setQuantity(0);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(customerRepository).findById(1L);
    }

    @Test
    void placeOrderFromDtoWithNegativeQuantity() {
        orderApiDto.getItems().get(0).setQuantity(-5);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.placeOrder(orderApiDto)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verify(bookRepository).findById(1L);
        verify(customerRepository).findById(1L);
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
    void placeOrderFromDtoBookRepositorySaveThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemApiDto.class))).thenReturn(orderItem);
        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderApiDto);
        });

        verify(bookRepository).save(book);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderFromDtoOrderRepositorySaveThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemApiDto.class))).thenReturn(orderItem);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderApiDto);
        });

        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrderFromDtoWithMultipleItems() {
        // Create second book and item
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Test Book 2");
        book2.setPrice(30.0f);
        book2.setQuantity(5);

        BookApiDto bookApiDto2 = new BookApiDto();
        bookApiDto2.setId(2L);

        OrderItemApiDto orderItemApiDto2 = new OrderItemApiDto();
        orderItemApiDto2.setBook(bookApiDto2);
        orderItemApiDto2.setQuantity(1);

        orderApiDto.getItems().add(orderItemApiDto2);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setBook(book2);
        orderItem2.setQuantity(1);
        orderItem2.setPrice(30.0f);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(orderMapper.toEntity(any(OrderItemApiDto.class)))
            .thenReturn(orderItem)
            .thenReturn(orderItem2);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        assertEquals(8, book.getQuantity()); // 10 - 2 = 8
        assertEquals(4, book2.getQuantity()); // 5 - 1 = 4
        verify(bookRepository, times(2)).save(any(Book.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrderFromDtoWithEmptyItemsList() {
        orderApiDto.setItems(List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(bookRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void placeOrderFromDtoWithNullItemsList() {
        orderApiDto.setItems(null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(bookRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
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
        doNothing().when(orderRepository).delete(order);

        orderService.deleteById(1L);

        assertEquals(12, book.getQuantity()); // 10 + 2 = 12
        assertEquals(18, book2.getQuantity()); // 15 + 3 = 18
        verify(orderRepository).findById(1L);
        verify(bookRepository, times(2)).save(any(Book.class));
        verify(orderRepository).delete(order);
    }

    @Test
    void findAllReturnsCorrectPageSize() {
        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomer(customer);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 2);

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.findAll(0, 1);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getSize());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void placeOrderFromDtoWithLargeQuantity() {
        book.setQuantity(1000);
        orderApiDto.getItems().get(0).setQuantity(999);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderMapper.toEntity(any(OrderItemApiDto.class))).thenReturn(orderItem);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderApiDto);

        assertNotNull(result);
        assertEquals(1, book.getQuantity()); // 1000 - 999 = 1
        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }
}