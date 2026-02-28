package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record SupplierResponseDto(
		UUID id,
		String name,
		String contactName,
		String email,
		String phone,
		String address,
		Boolean active,
		int productCount,
		Instant createdAt,
		Instant updatedAt) implements Serializable {
}
