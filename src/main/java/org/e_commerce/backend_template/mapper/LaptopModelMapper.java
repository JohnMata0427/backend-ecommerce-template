package org.e_commerce.backend_template.mapper;

import org.e_commerce.backend_template.dto.LaptopModelRequestDto;
import org.e_commerce.backend_template.dto.LaptopModelResponseDto;
import org.e_commerce.backend_template.entity.LaptopModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LaptopModelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LaptopModel toEntity(LaptopModelRequestDto dto);

    LaptopModelResponseDto toResponseDto(LaptopModel entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(LaptopModelRequestDto dto, @MappingTarget LaptopModel entity);
}
