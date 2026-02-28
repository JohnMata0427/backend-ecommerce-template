package org.e_commerce.backend_template.repository;

import java.util.Optional;
import java.util.UUID;

import org.e_commerce.backend_template.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

	Optional<Supplier> findByEmailIgnoreCase(String email);

	Page<Supplier> findByActiveTrue(Pageable pageable);

	Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

	boolean existsByEmailIgnoreCase(String email);
}
