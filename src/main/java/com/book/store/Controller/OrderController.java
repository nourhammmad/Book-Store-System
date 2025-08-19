package com.book.store.Controller;

import com.book.store.server.api.OrdersApi; // OpenAPI-generated interface
import com.book.store.server.dto.OrderApiDto;
import com.book.store.server.dto.OrdersApiDto;
import com.book.store.Service.OrderService;
import com.book.store.Entity.Order;
import com.book.store.Mapper.OrderMapper;
import java.util.List;
import java.util.stream.Collectors;

import com.book.store.server.dto.UpdateOrderRequestApiDto;
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

    // DELETE /orders/{id}

    @Override
    public ResponseEntity<Void> deleteOrderById(Long id) {
        orderService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // GET /orders?page=0&size=10
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

    // GET /orders/{id}
    @Override
    public ResponseEntity<OrderApiDto> findOrderById(Long id) {
        Order order = orderService.findById(id);
        return new ResponseEntity<>(orderMapper.toDTO(order), HttpStatus.OK);
    }

    // POST /orders
    //http://localhost:8080/orders
    //{
    //  "customer": {
    //    "id": 1
    //  },
    //  "items": [
    //    {
    //      "book": { "id": 2 },
    //      "quantity": 1
    //    }
    //  ]
    //}
    @Override
    public ResponseEntity<OrderApiDto> placeOrder(OrderApiDto orderApiDto) {
        Order createdOrder = orderService.placeOrder(orderApiDto);
        return new ResponseEntity<>(orderMapper.toDTO(createdOrder), HttpStatus.CREATED);
    }

    // PUT /orders/{id}
    @Override
    public ResponseEntity<OrderApiDto> updateOrder(Long id, UpdateOrderRequestApiDto updateOrderRequestApiDto) {
//        Order updatedOrder = orderService.updateOrder(id.longValue(), updateOrderRequestApiDto);
//        return new ResponseEntity<>(orderMapper.toDTO(updatedOrder), HttpStatus.OK);
        return null;
    }


}
