package org.e_commerce.backend_template.repository;

import java.util.UUID;

import org.e_commerce.backend_template.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

	Page<Brand> findByActiveTrue(Pageable pageable);

	Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);

	boolean existsByNameIgnoreCase(String name);
}