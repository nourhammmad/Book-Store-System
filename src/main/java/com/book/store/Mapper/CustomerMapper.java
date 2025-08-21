package com.book.store.Mapper;

import com.book.store.server.dto.CustomerApiDto;
import com.book.store.Entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Map DTO -> Entity
    Customer toEntity(CustomerApiDto dto);

    // Map Entity -> DTO, omit ID in response
    @Mapping(target = "id", ignore = true)
    CustomerApiDto toDTO(Customer customer);

    // Partial update (ignore null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate(CustomerApiDto dto, @MappingTarget Customer customer);
}
