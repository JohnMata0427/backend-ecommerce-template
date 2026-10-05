package org.e_commerce.backend_template.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.e_commerce.backend_template.entity.LaptopModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaptopModelRepository extends JpaRepository<LaptopModel, UUID> {

    Optional<LaptopModel> findByBrandNameIgnoreCaseAndSeriesIgnoreCaseAndModelNumberIgnoreCase(
        String brandName, String series, String modelNumber
    );

    Page<LaptopModel> findByBrandNameIgnoreCase(String brandName, Pageable pageable);

    Page<LaptopModel> findByModelNumberContainingIgnoreCase(String modelNumber, Pageable pageable);

    List<LaptopModel> findByBrandNameIgnoreCaseOrderByModelNumberAsc(String brandName);

    boolean existsByBrandNameIgnoreCaseAndSeriesIgnoreCaseAndModelNumberIgnoreCase(
        String brandName, String series, String modelNumber
    );
}
