package org.e_commerce.backend_template.repository;

import java.util.UUID;

import org.e_commerce.backend_template.entity.Subcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {

	Page<Subcategory> findByCategoryId(UUID categoryId, Pageable pageable);

	Page<Subcategory> findByActiveTrue(Pageable pageable);

	Page<Subcategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

	boolean existsByNameIgnoreCaseAndCategoryId(String name, UUID categoryId);
}
