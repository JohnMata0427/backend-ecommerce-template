package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponseDto(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String sku,
    UUID subcategoryId,
    String subcategoryName,
    UUID supplierId,
    String supplierName,
    UUID brandId,
    String brandName,
    String imageUrl,
    String imagePublicId,
    Boolean active,
    Instant createdAt,
    Instant updatedAt) implements Serializable {
}
