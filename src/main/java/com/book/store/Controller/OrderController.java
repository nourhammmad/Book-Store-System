package com.book.store.Controller;

import com.book.store.server.api.OrdersApi; // OpenAPI-generated interface
import com.book.store.server.dto.OrderApiDto;
import com.book.store.server.dto.OrdersApiDto;
import com.book.store.Service.OrderService;
import com.book.store.Entity.Order;
import com.book.store.Mapper.OrderMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Override
    public ResponseEntity<Void> deleteOrderById(Integer id) {
        return null;
    }

    //http://localhost:8080/orders?page=0&size=10
    @Override
    public ResponseEntity<OrdersApiDto> findAllOrders(Integer page, Integer size) {
        List<Order> orders = orderService.findAll(page, size).getContent();
        List<OrderApiDto> dtoList = orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        OrdersApiDto ordersApiDto = new OrdersApiDto();
        ordersApiDto.setOrders(dtoList);

        return new ResponseEntity<>(ordersApiDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderApiDto> findOrderById(Integer id) {
        return null;
    }

    //http://localhost:8080/orders
    //{
    //  "customer": {
    //    "id": 1
    //  },
    //  "book": {
    //    "id": 2
    //  },
    //  "quantity": 3
    //}
    @Override
    public ResponseEntity<OrderApiDto> placeOrder(OrderApiDto orderApiDto) {
        Order createdOrder = orderService.placeOrder(orderApiDto);
        return new ResponseEntity<>(orderMapper.toDTO(createdOrder), HttpStatus.CREATED);
    }


}
