package com.book.store.controller;

import com.book.store.server.api.OrdersApi;
import com.book.store.server.dto.OrderResponseApiDto;
import com.book.store.server.dto.OrderRequestApiDto;
import com.book.store.server.dto.PaginatedOrderResponseApiDto;
import com.book.store.service.OrderService;
import com.book.store.entity.Order;
import com.book.store.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

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
}