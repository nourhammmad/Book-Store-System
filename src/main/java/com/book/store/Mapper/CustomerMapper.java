package com.book.store.Mapper;

import com.book.store.server.dto.CustomerApiDto;
import com.book.store.Entity.Customer;
import com.book.store.server.dto.CustomerReferenceApiDto;
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

    // ✅ Entity -> Reference DTO (only minimal fields)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    CustomerReferenceApiDto toReferenceDto(Customer customerApiDto);

    // Map Entity -> DTO, omit ID in response
    CustomerApiDto toDTO(Customer customer);

    // Partial update (ignore null fields)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdate(CustomerApiDto dto, @MappingTarget Customer customer);
}
