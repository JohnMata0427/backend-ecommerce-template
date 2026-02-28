package org.e_commerce.backend_template.repository;

import java.util.Optional;
import java.util.UUID;

import org.e_commerce.backend_template.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  Optional<Product> findBySku(String sku);

  Page<Product> findByActiveTrue(Pageable pageable);

  Page<Product> findBySubcategoryId(UUID subcategoryId, Pageable pageable);

  Page<Product> findBySupplierId(UUID supplierId, Pageable pageable);

  Page<Product> findBySubcategory_Category_Id(UUID categoryId, Pageable pageable);

  Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

  boolean existsBySku(String sku);

  Page<Product> findByBrandId(UUID brandId, Pageable pageable);
}
