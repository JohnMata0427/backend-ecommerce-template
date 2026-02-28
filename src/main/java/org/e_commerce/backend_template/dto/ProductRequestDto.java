package org.e_commerce.backend_template.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequestDto(

    @NotBlank(message = "El nombre del producto es obligatorio") @Size(max = 255, message = "El nombre no puede exceder 255 caracteres") String name,

    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres") String description,

    @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0") BigDecimal price,

    @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,

    @NotBlank(message = "El SKU es obligatorio") @Size(max = 100, message = "El SKU no puede exceder 100 caracteres") String sku,

    UUID subcategoryId,

    UUID supplierId,

    UUID brandId,

    Boolean active) {
}
