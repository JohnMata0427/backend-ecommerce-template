package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record BrandResponseDto(
		UUID id,
		String name,
		String description,
		Boolean active,
		int productCount,
		Instant createdAt,
		Instant updatedAt) implements Serializable {
}