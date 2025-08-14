package com.book.store.Mapper;

import com.examle.demo.server.dto.OrderApiDto;
import com.examle.demo.server.dto.OrderItemApiDto;
import com.book.store.Entity.Order;
import com.book.store.Entity.OrderItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // Map OrderApiDto -> Order entity
    Order toEntity(OrderApiDto orderApiDto);

    // Map Order entity -> OrderApiDto
    OrderApiDto toDTO(Order order);

    // Partial update (ignores null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(OrderApiDto orderApiDto, @MappingTarget Order order);

    // Map OrderItemApiDto -> OrderItem
    OrderItem toEntity(OrderItemApiDto orderItemApiDto);

    // Map OrderItem -> OrderItemApiDto
    OrderItemApiDto toDTO(OrderItem orderItem);

    // Conversion helpers for OffsetDateTime <-> LocalDateTime
    default LocalDateTime map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(OffsetDateTime.now().getOffset());
    }
}
