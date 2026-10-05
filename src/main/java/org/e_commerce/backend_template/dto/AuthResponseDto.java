package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.util.UUID;

import org.e_commerce.backend_template.entity.Role;

public record AuthResponseDto(
    String token,
    String tokenType,
    UUID userId,
    String username,
    String email,
    String fullName,
    Role role) implements Serializable {
}
