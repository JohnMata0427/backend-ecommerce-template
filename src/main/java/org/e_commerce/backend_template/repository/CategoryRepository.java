package org.e_commerce.backend_template.repository;

import java.util.UUID;

import org.e_commerce.backend_template.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

	Page<Category> findByActiveTrue(Pageable pageable);

	Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

	boolean existsByNameIgnoreCase(String name);
}
