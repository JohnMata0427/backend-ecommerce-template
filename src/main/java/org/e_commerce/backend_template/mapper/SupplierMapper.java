package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.SupplierRequestDto;
import org.e_commerce.backend_template.dto.SupplierResponseDto;
import org.e_commerce.backend_template.entity.Supplier;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Supplier toEntity(SupplierRequestDto dto);

	@Mapping(target = "productCount", expression = "java(supplier.getProducts() != null ? supplier.getProducts().size() : 0)")
	SupplierResponseDto toResponseDto(Supplier supplier);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEntityFromDto(SupplierRequestDto dto, @MappingTarget Supplier supplier);
}
