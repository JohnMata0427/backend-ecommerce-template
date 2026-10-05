package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.ProductImageDto;
import org.e_commerce.backend_template.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    ProductImageDto toDto(ProductImage entity);
}
