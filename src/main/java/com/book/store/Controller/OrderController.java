package com.book.store.Controller;

import com.book.store.DTO.OrderDTO;
import com.book.store.Entity.Order;
import com.book.store.Mapper.OrderMapper;
import com.book.store.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    // http://localhost:8080/api/orders?page=0&size=5
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.findAll(page, size);
        Page<OrderDTO> orderDTOs = orders.map(orderMapper::toDTO);
        return ResponseEntity.ok(orderDTOs);
    }

    // ✅ Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    // http://localhost:8080/api/orders?customerId=1&bookId=2&quantity=2
    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(
            @RequestParam Long customerId,
            @RequestParam Long bookId,
            @RequestParam int quantity) {
        Order order = orderService.placeOrder(customerId, bookId, quantity);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    //  Delete order by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        orderService.delete(order);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
