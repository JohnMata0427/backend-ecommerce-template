package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record LaptopModelResponseDto(
    UUID id,
    String brandName,
    String series,
    String modelNumber,
    String notes,
    Instant createdAt,
    Instant updatedAt) implements Serializable {
}
