package com.book.store.mapper;

import com.book.store.entity.Order;
import com.book.store.entity.OrderItem;
import com.book.store.server.dto.OrderRequestApiDto;
import com.book.store.server.dto.OrderResponseApiDto;
import com.book.store.server.dto.OrderItemRequestApiDto;
import com.book.store.server.dto.OrderItemResponseApiDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderMapper {
    // Map OrderRequestApiDto -> Order entity (for create/update)
    @Mapping(target = "items", source = "items")
    Order toEntity(OrderRequestApiDto orderRequestApiDto);

    // Map Order entity -> OrderResponseApiDto (for responses)
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(order.getTotalPrice())")
    @Mapping(target = "status", source = "status")
    OrderResponseApiDto toResponseDto(Order order);

    // Partial update (ignores null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(OrderRequestApiDto orderRequestApiDto, @MappingTarget Order order);

    // === OrderItem mappings ===
    OrderItem toEntity(OrderItemRequestApiDto orderItemRequestApiDto);

    OrderItemResponseApiDto toResponseDto(OrderItem orderItem);

    List<OrderItemResponseApiDto> toResponseDto(List<OrderItem> items);

    List<OrderItem> toEntityFromRequest(List<OrderItemRequestApiDto> items);

    // === Conversion helpers for OffsetDateTime <-> LocalDateTime ===

    default LocalDateTime map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(OffsetDateTime.now().getOffset());
    }

    // === STATUS ENUM MAPPINGS ===

    default OrderResponseApiDto.StatusEnum map(Order.OrderStatus status) {
        if (status == null) return null;
        return switch (status) {
            case PLACED -> OrderResponseApiDto.StatusEnum.PLACED;
            case PROCESSING -> OrderResponseApiDto.StatusEnum.PROCESSING;
            case SHIPPED -> OrderResponseApiDto.StatusEnum.SHIPPED;
            case DELIVERED -> OrderResponseApiDto.StatusEnum.DELIVERED;
            case CANCELLED -> OrderResponseApiDto.StatusEnum.CANCELLED;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}