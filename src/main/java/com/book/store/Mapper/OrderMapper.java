package com.book.store.Mapper;

import com.book.store.DTO.OrderDTO;
import com.book.store.Entity.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderMapper {
    public OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
//                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
//                .bookId(order.getBook() != null ? order.getBook().getId() : null)
                .quantity(order.getQuantity())
                .orderDate(LocalDateTime.now())
                .build();
    }

}
