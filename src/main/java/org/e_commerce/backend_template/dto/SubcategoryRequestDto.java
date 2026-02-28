package org.e_commerce.backend_template.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubcategoryRequestDto(

		@NotBlank(message = "El nombre de la subcategoría es obligatorio") @Size(max = 150, message = "El nombre no puede exceder 150 caracteres") String name,

		@Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres") String description,

		@NotNull(message = "El ID de la categoría es obligatorio") UUID categoryId,

		Boolean active) {
}
