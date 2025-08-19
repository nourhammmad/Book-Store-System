package com.book.store.Mapper;

import com.book.store.server.dto.OrderApiDto;
import com.book.store.server.dto.OrderItemApiDto;
import com.book.store.Entity.Order;
import com.book.store.Entity.OrderItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // Map OrderApiDto -> Order entity including items
    @Mapping(target = "items", source = "items")
    @Mapping(target = "status", source = "status")
    Order toEntity(OrderApiDto orderApiDto);

    // Map Order entity -> OrderApiDto including items
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(order.getTotalPrice())")
    @Mapping(target = "status", source = "status")
    OrderApiDto toDTO(Order order);

    // Partial update (ignores null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(OrderApiDto orderApiDto, @MappingTarget Order order);

    // Map OrderItemApiDto -> OrderItem
    OrderItem toEntity(OrderItemApiDto orderItemApiDto);

    // Map OrderItem -> OrderItemApiDto
    OrderItemApiDto toDTO(OrderItem orderItem);

    // Map list of OrderItem -> list of OrderItemApiDto
    List<OrderItemApiDto> toDTO(List<OrderItem> items);

    // Map list of OrderItemApiDto -> list of OrderItem
    List<OrderItem> toEntity(List<OrderItemApiDto> items);

    // Conversion helpers for OffsetDateTime <-> LocalDateTime
    default LocalDateTime map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(OffsetDateTime.now().getOffset());
    }

    // === STATUS ENUM MAPPINGS ===
    default Order.OrderStatus map(OrderApiDto.StatusEnum statusEnum) {
        if (statusEnum == null) return null;
        switch (statusEnum) {
            case PLACED: return Order.OrderStatus.PLACED;
            case PROCESSING: return Order.OrderStatus.PROCESSING;
            case COMPLETED: return Order.OrderStatus.COMPLETED;
            case CANCELLED: return Order.OrderStatus.CANCELLED;
            default: throw new IllegalArgumentException("Unknown status: " + statusEnum);
        }
    }

    default OrderApiDto.StatusEnum map(Order.OrderStatus status) {
        if (status == null) return null;
        switch (status) {
            case PLACED: return OrderApiDto.StatusEnum.PLACED;
            case PROCESSING: return OrderApiDto.StatusEnum.PROCESSING;
            case COMPLETED: return OrderApiDto.StatusEnum.COMPLETED;
            case CANCELLED: return OrderApiDto.StatusEnum.CANCELLED;
            default: throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
