package com.book.store.mapper;

import com.book.store.server.dto.CustomerApiDto;
import com.book.store.entity.Customer;
import com.book.store.server.dto.CustomerReferenceApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CustomerMapper {
    // Map DTO -> Entity
    Customer toEntity(CustomerApiDto dto);

    // ✅ Entity -> Reference DTO (only minimal fields)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    CustomerReferenceApiDto toReferenceDto(Customer customerApiDto);

    @org.mapstruct.ObjectFactory
    default CustomerReferenceApiDto createCustomerReferenceApiDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerReferenceApiDto(customer.getId(), customer.getUsername());
    }

    // Map Entity -> DTO, omit ID in response
    CustomerApiDto toDTO(Customer customer);

    // Partial update (ignore null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate(CustomerApiDto dto, @MappingTarget Customer customer);
}
