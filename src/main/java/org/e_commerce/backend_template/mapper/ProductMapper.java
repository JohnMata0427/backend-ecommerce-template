package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.ProductRequestDto;
import org.e_commerce.backend_template.dto.ProductResponseDto;
import org.e_commerce.backend_template.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "imagePublicId", ignore = true)
  @Mapping(target = "subcategory", ignore = true)
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "brand", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product toEntity(ProductRequestDto dto);

  @Mapping(target = "subcategoryId", source = "subcategory.id")
  @Mapping(target = "subcategoryName", source = "subcategory.name")
  @Mapping(target = "supplierId", source = "supplier.id")
  @Mapping(target = "supplierName", source = "supplier.name")
  @Mapping(target = "brandId", source = "brand.id")
  @Mapping(target = "brandName", source = "brand.name")
  ProductResponseDto toResponseDto(Product product);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "imageUrl", ignore = true)
  @Mapping(target = "imagePublicId", ignore = true)
  @Mapping(target = "subcategory", ignore = true)
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "brand", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromDto(ProductRequestDto dto, @MappingTarget Product product);
}