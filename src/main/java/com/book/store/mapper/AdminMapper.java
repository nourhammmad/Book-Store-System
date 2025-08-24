package com.book.store.mapper;

import com.book.store.entity.Admin;
import com.book.store.server.dto.AdminApiDto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AdminMapper {
    Admin toEntity(AdminApiDto dto);

    AdminApiDto toDTO(Admin admin);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Admin partialUpdate(AdminApiDto dto, @MappingTarget Admin admin);
}
