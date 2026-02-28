package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.CategoryRequestDto;
import org.e_commerce.backend_template.dto.CategoryResponseDto;
import org.e_commerce.backend_template.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "subcategories", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Category toEntity(CategoryRequestDto dto);

	@Mapping(target = "subcategoryCount", expression = "java(category.getSubcategories() != null ? category.getSubcategories().size() : 0)")
	CategoryResponseDto toResponseDto(Category category);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "subcategories", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEntityFromDto(CategoryRequestDto dto, @MappingTarget Category category);
}
