package org.e_commerce.backend_template.service;

import java.util.UUID;

import org.e_commerce.backend_template.dto.BrandRequestDto;
import org.e_commerce.backend_template.dto.BrandResponseDto;
import org.e_commerce.backend_template.entity.Brand;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.BrandMapper;
import org.e_commerce.backend_template.repository.BrandRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BrandService {

	private static final String BRAND_NOT_FOUND = "Marca no encontrada con id: %s";
	private static final String DUPLICATE_NAME = "Ya existe una marca con el nombre: %s";

	private final BrandRepository brandRepository;
	private final BrandMapper brandMapper;

	@Transactional
	public BrandResponseDto createBrand(final BrandRequestDto requestDto) {
		if (brandRepository.existsByNameIgnoreCase(requestDto.name())) {
			throw new AppException(DUPLICATE_NAME.formatted(requestDto.name()), HttpStatus.CONFLICT);
		}

		final Brand brand = brandMapper.toEntity(requestDto);
		final Brand savedBrand = brandRepository.save(brand);
		log.info("Marca creada: id={}, name={}", savedBrand.getId(), savedBrand.getName());
		return brandMapper.toResponseDto(savedBrand);
	}

	@Cacheable(value = "brands", key = "#id")
	public BrandResponseDto getBrandById(final UUID id) {
		final Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new AppException(BRAND_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		return brandMapper.toResponseDto(brand);
	}

	public Page<BrandResponseDto> getAllBrands(final Pageable pageable) {
		return brandRepository.findAll(pageable)
				.map(brandMapper::toResponseDto);
	}

	public Page<BrandResponseDto> getActiveBrands(final Pageable pageable) {
		return brandRepository.findByActiveTrue(pageable)
				.map(brandMapper::toResponseDto);
	}

	public Page<BrandResponseDto> searchBrandsByName(final String name, final Pageable pageable) {
		return brandRepository.findByNameContainingIgnoreCase(name, pageable)
				.map(brandMapper::toResponseDto);
	}

	@Transactional
	@CachePut(value = "brands", key = "#id")
	public BrandResponseDto updateBrand(final UUID id, final BrandRequestDto requestDto) {
		final Brand existingBrand = brandRepository.findById(id)
				.orElseThrow(() -> new AppException(BRAND_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		if (requestDto.name() != null && !requestDto.name().equalsIgnoreCase(existingBrand.getName())) {
			if (brandRepository.existsByNameIgnoreCase(requestDto.name())) {
				throw new AppException(DUPLICATE_NAME.formatted(requestDto.name()), HttpStatus.CONFLICT);
			}
		}

		brandMapper.updateEntityFromDto(requestDto, existingBrand);
		final Brand updatedBrand = brandRepository.save(existingBrand);
		log.info("Marca actualizada: id={}, name={}", updatedBrand.getId(), updatedBrand.getName());
		return brandMapper.toResponseDto(updatedBrand);
	}

	@Transactional
	@CacheEvict(value = "brands", key = "#id")
	public void deleteBrand(final UUID id) {
		final Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new AppException(BRAND_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		brandRepository.delete(brand);
		log.info("Marca eliminada: id={}", id);
	}
}