package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.e_commerce.backend_template.entity.ProductCondition;

public record ProductResponseDto(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    BigDecimal costPrice,
    Integer stock,
    Integer minStockAlert,
    String sku,
    String partNumber,
    ProductCondition condition,
    String location,
    Boolean isElectrical,
    Integer warrantyMonths,
    String compatibilityNotes,
    UUID subcategoryId,
    String subcategoryName,
    UUID supplierId,
    String supplierName,
    UUID brandId,
    String brandName,
    String imageUrl,
    String imagePublicId,
    List<ProductImageDto> images,
    Set<LaptopModelResponseDto> compatibleModels,
    Boolean active,
    Instant createdAt,
    Instant updatedAt) implements Serializable {
}
