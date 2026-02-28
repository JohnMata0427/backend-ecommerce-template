package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.BrandRequestDto;
import org.e_commerce.backend_template.dto.BrandResponseDto;
import org.e_commerce.backend_template.entity.Brand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BrandMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Brand toEntity(BrandRequestDto dto);

	@Mapping(target = "productCount", expression = "java(brand.getProducts() != null ? brand.getProducts().size() : 0)")
	BrandResponseDto toResponseDto(Brand brand);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEntityFromDto(BrandRequestDto dto, @MappingTarget Brand brand);
}