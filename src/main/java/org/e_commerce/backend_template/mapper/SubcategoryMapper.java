package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.SubcategoryRequestDto;
import org.e_commerce.backend_template.dto.SubcategoryResponseDto;
import org.e_commerce.backend_template.entity.Subcategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Subcategory toEntity(SubcategoryRequestDto dto);

	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "categoryName", source = "category.name")
	@Mapping(target = "productCount", expression = "java(subcategory.getProducts() != null ? subcategory.getProducts().size() : 0)")
	SubcategoryResponseDto toResponseDto(Subcategory subcategory);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEntityFromDto(SubcategoryRequestDto dto, @MappingTarget Subcategory subcategory);
}
