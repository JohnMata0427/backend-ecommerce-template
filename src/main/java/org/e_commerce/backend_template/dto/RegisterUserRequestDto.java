package org.e_commerce.backend_template.dto;

import org.e_commerce.backend_template.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDto(
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    String username,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    String fullName,

    Role role) {
}
