package com.book.store.Controller;

import com.book.store.server.api.OrdersApi; // Generated from OpenAPI
import com.book.store.server.dto.OrderResponseApiDto;
import com.book.store.server.dto.OrderRequestApiDto;


import com.book.store.Service.OrderService;
import com.book.store.Entity.Order;
import com.book.store.Mapper.OrderMapper;
import com.book.store.server.dto.PaginatedOrderResponseApiDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    // ✅ GET /order
    @Override
    public ResponseEntity<PaginatedOrderResponseApiDto> findAllOrders(Integer page, Integer size) {
        var orderPage = orderService.findAll(page, size);

        PaginatedOrderResponseApiDto response = new PaginatedOrderResponseApiDto();
        response.setContent(
                orderPage.getContent()
                        .stream()
                        .map(orderMapper::toResponseDto)
                        .collect(Collectors.toList())
        );
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((int) orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // ✅ POST /order
    @Override
    public ResponseEntity<OrderResponseApiDto> placeOrder(OrderRequestApiDto orderRequestApiDto) {
        var createdOrder = orderService.placeOrder(orderRequestApiDto);
        return new ResponseEntity<>(orderMapper.toResponseDto(createdOrder), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<OrderResponseApiDto>> findPreviousOrders() {
        // Fetch previous orders from the service
        List<Order> orders = orderService.GetPreviousOrders();

        // Map each Order entity to the new OrderResponseApiDto using the new mapper
        List<OrderResponseApiDto> dtoList = orders.stream()
                .map(orderMapper::toResponseDto)
                .toList();

        // Return the list directly in the response
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }


    // POST /orders
    //http://localhost:8080/orders

    @Override
    public ResponseEntity<OrderResponseApiDto> findOrderById(Long id) {
        var order = orderService.findById(id);
        return ResponseEntity.ok(orderMapper.toResponseDto(order));
    }

    // ✅ DELETE /order/{id}
    @Override
    public ResponseEntity<Void> deleteOrderById(Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
