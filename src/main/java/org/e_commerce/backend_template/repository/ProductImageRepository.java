package org.e_commerce.backend_template.repository;

import java.util.List;
import java.util.UUID;

import org.e_commerce.backend_template.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(UUID productId);

    void deleteByProductId(UUID productId);
}
