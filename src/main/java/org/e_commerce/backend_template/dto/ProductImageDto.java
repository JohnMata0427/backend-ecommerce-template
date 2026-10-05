package org.e_commerce.backend_template.dto;

import java.io.Serializable;
import java.util.UUID;

public record ProductImageDto(
    UUID id,
    String imageUrl,
    String imagePublicId,
    Boolean isPrimary,
    Integer sortOrder) implements Serializable {
}
