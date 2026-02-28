package org.e_commerce.backend_template.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequestDto(

		@NotBlank(message = "El nombre del proveedor es obligatorio") @Size(max = 255, message = "El nombre no puede exceder 255 caracteres") String name,

		@Size(max = 255, message = "El nombre de contacto no puede exceder 255 caracteres") String contactName,

		@NotBlank(message = "El email es obligatorio") @Email(message = "El email debe ser válido") @Size(max = 255, message = "El email no puede exceder 255 caracteres") String email,

		@Size(max = 50, message = "El teléfono no puede exceder 50 caracteres") String phone,

		@Size(max = 5000, message = "La dirección no puede exceder 5000 caracteres") String address,

		Boolean active) {
}
