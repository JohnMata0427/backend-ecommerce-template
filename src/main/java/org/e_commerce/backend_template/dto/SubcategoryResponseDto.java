package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record SubcategoryResponseDto(
		UUID id,
		String name,
		String description,
		UUID categoryId,
		String categoryName,
		Boolean active,
		int productCount,
		Instant createdAt,
		Instant updatedAt) implements Serializable {
}
