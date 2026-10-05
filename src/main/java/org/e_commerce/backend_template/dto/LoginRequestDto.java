package org.e_commerce.backend_template.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "El usuario o email es obligatorio")
    String username,

    @NotBlank(message = "La contraseña es obligatoria")
    String password) {
}
