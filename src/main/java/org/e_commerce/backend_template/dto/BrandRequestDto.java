package org.e_commerce.backend_template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BrandRequestDto(

		@NotBlank(message = "El nombre de la marca es obligatorio") @Size(max = 150, message = "El nombre no puede exceder 150 caracteres") String name,

		@Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres") String description,

		Boolean active) {
}