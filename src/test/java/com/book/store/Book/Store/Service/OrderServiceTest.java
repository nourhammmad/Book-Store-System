//package com.book.store.Book.Store.Service;
//
//import com.book.store.Book.Store.Entity.Book;
//import com.book.store.Book.Store.Entity.Orders;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class OrderServiceTest {
//
//    private OrderService orderService; // Will be mocked or a real instance depending on setup
//
//    @BeforeEach
//    void setUp() {
//        orderService = mock(OrderService.class);
//    }
//
//    @Test
//    void findAll() {
//        Orders order = new Orders();
//        order.setId(UUID.randomUUID());
//        when(orderService.findAll()).thenReturn(List.of(order));
//
//        List<Orders> result = orderService.findAll();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(order.getId(), result.get(0).getId());
//        verify(orderService, times(1)).findAll();
//    }
//
//    @Test
//    void delete() {
//        UUID orderId = UUID.randomUUID();
//        doNothing().when(orderService).delete(orderId);
//
//        orderService.delete(orderId);
//
//        verify(orderService, times(1)).delete(orderId);
//    }
//
//    @Test
//    void placeOrder() {
//        Book book = new Book();
//        book.setId(UUID.randomUUID());
//        book.setTitle("Java Basics");
//
//        Orders order = new Orders();
//        order.setBook(book);
//        order.setQuantity(2);
//        order.setTotalPrice(50.0f);
//        order.setOrderDate(LocalDateTime.now());
//
//        when(orderService.placeOrder(order)).thenReturn(order);
//
//        Order savedOrder = orderService.placeOrder(order);
//
//        assertNotNull(savedOrder);
//        assertEquals(book.getId(), savedOrder.getBook().getId());
//        verify(orderService, times(1)).placeOrder(order);
//    }
//
//    @Test
//    void findById() {
//        UUID orderId = UUID.randomUUID();
//        Orders order = new Orders();
//        order.setId(orderId);
//
//        when(orderService.findById(orderId)).thenReturn(Optional.of(order));
//
//        Optional<Orders> result = Optional.ofNullable(orderService.findById(orderId));
//
//        assertTrue(result.isPresent());
//        assertEquals(orderId, result.get().getId());
//        verify(orderService, times(1)).findById(orderId);
//    }
//}
