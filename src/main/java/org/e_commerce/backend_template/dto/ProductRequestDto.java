package org.e_commerce.backend_template.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import org.e_commerce.backend_template.entity.ProductCondition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequestDto(

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
    String description,

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    BigDecimal price,

    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    BigDecimal costPrice,

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock,

    @Min(value = 0, message = "El stock mínimo de alerta no puede ser negativo")
    Integer minStockAlert,

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 100, message = "El SKU no puede exceder 100 caracteres")
    String sku,

    @Size(max = 100, message = "El número de parte (Part Number) no puede exceder 100 caracteres")
    String partNumber,

    ProductCondition condition,

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    String location,

    Boolean isElectrical,

    @Min(value = 0, message = "Los meses de garantía no pueden ser negativos")
    Integer warrantyMonths,

    @Size(max = 5000, message = "Las notas de compatibilidad no pueden exceder 5000 caracteres")
    String compatibilityNotes,

    UUID subcategoryId,

    UUID supplierId,

    UUID brandId,

    Set<UUID> compatibleModelIds,

    Boolean active) {
}
