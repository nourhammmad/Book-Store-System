package com.book.store.controller;

import com.book.store.entity.Order;
import com.book.store.server.api.OrdersApi;
import com.book.store.server.dto.*;
import com.book.store.service.OrderService;
import com.book.store.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    // Contract-defined endpoints
    @Override
    public ResponseEntity<PaginatedOrderResponseApiDto> findAllOrders(Integer page, Integer size) {
        var orderPage = orderService.findAll(page, size);
        var content = orderPage.getContent()
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();
        var totalElements = orderPage.getTotalElements();
        var totalPages = orderPage.getTotalPages();

        PaginatedOrderResponseApiDto response = new PaginatedOrderResponseApiDto(content, page, size, totalElements, totalPages);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<OrderResponseApiDto> placeOrder(OrderRequestApiDto orderRequestApiDto) {
        var createdOrder = orderService.placeOrder(orderRequestApiDto);
        return new ResponseEntity<>(orderMapper.toResponseDto(createdOrder), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<OrderResponseApiDto>> findPreviousOrders() {
        List<Order> orders = orderService.getPreviousOrders();
        List<OrderResponseApiDto> dtoList = orders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @Override
    public ResponseEntity<OrderResponseApiDto> findOrderById(Long id) {
        var order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderMapper.toResponseDto(order));
    }

    @Override
    public ResponseEntity<Void> deleteOrderById(Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Enhanced order management endpoints (using contract-compliant paths)
    @Override
    public ResponseEntity<OrderResponseApiDto> updateOrderStatus(Long id, UpdateOrderStatusRequestApiDto updateOrderStatusRequestApiDto) {
        String status = String.valueOf(updateOrderStatusRequestApiDto.getStatus());
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(orderMapper.toResponseDto(updatedOrder));
    }

    @Override
    public ResponseEntity<OrderResponseApiDto> cancelOrder(Long id, CancelOrderRequestApiDto cancelOrderRequestApiDto) {
        String reason = cancelOrderRequestApiDto != null ? cancelOrderRequestApiDto.getReason() : null;
        Order cancelledOrder = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(orderMapper.toResponseDto(cancelledOrder));
    }
}