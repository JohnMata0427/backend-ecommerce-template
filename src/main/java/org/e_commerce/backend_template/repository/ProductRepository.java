package org.e_commerce.backend_template.repository;

import java.util.Optional;
import java.util.UUID;

import org.e_commerce.backend_template.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  Optional<Product> findBySku(String sku);

  Optional<Product> findByPartNumberIgnoreCase(String partNumber);

  Page<Product> findByActiveTrue(Pageable pageable);

  Page<Product> findBySubcategoryId(UUID subcategoryId, Pageable pageable);

  Page<Product> findBySupplierId(UUID supplierId, Pageable pageable);

  Page<Product> findBySubcategory_Category_Id(UUID categoryId, Pageable pageable);

  Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Product> findByPartNumberContainingIgnoreCase(String partNumber, Pageable pageable);

  boolean existsBySku(String sku);

  Page<Product> findByBrandId(UUID brandId, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.stock <= p.minStockAlert AND p.active = true")
  Page<Product> findLowStockProducts(Pageable pageable);

  @Query("SELECT p FROM Product p JOIN p.compatibleModels m WHERE m.id = :laptopModelId AND p.active = true")
  Page<Product> findByCompatibleModelId(@Param("laptopModelId") UUID laptopModelId, Pageable pageable);
}
