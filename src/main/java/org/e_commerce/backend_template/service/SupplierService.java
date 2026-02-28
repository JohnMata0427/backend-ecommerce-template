package org.e_commerce.backend_template.service;

import java.util.UUID;

import org.e_commerce.backend_template.dto.SupplierRequestDto;
import org.e_commerce.backend_template.dto.SupplierResponseDto;
import org.e_commerce.backend_template.entity.Supplier;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.SupplierMapper;
import org.e_commerce.backend_template.repository.SupplierRepository;
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
public class SupplierService {

	private static final String SUPPLIER_NOT_FOUND = "Proveedor no encontrado con id: %s";
	private static final String DUPLICATE_EMAIL = "Ya existe un proveedor con el email: %s";

	private final SupplierRepository supplierRepository;
	private final SupplierMapper supplierMapper;

	@Transactional
	public SupplierResponseDto createSupplier(final SupplierRequestDto requestDto) {
		if (supplierRepository.existsByEmailIgnoreCase(requestDto.email())) {
			throw new AppException(DUPLICATE_EMAIL.formatted(requestDto.email()), HttpStatus.CONFLICT);
		}

		final Supplier supplier = supplierMapper.toEntity(requestDto);
		final Supplier savedSupplier = supplierRepository.save(supplier);
		log.info("Proveedor creado: id={}, name={}", savedSupplier.getId(), savedSupplier.getName());
		return supplierMapper.toResponseDto(savedSupplier);
	}

	@Cacheable(value = "suppliers", key = "#id")
	public SupplierResponseDto getSupplierById(final UUID id) {
		final Supplier supplier = supplierRepository.findById(id)
				.orElseThrow(() -> new AppException(SUPPLIER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		return supplierMapper.toResponseDto(supplier);
	}

	public Page<SupplierResponseDto> getAllSuppliers(final Pageable pageable) {
		return supplierRepository.findAll(pageable)
				.map(supplierMapper::toResponseDto);
	}

	public Page<SupplierResponseDto> getActiveSuppliers(final Pageable pageable) {
		return supplierRepository.findByActiveTrue(pageable)
				.map(supplierMapper::toResponseDto);
	}

	public Page<SupplierResponseDto> searchSuppliersByName(final String name, final Pageable pageable) {
		return supplierRepository.findByNameContainingIgnoreCase(name, pageable)
				.map(supplierMapper::toResponseDto);
	}

	@Transactional
	@CachePut(value = "suppliers", key = "#id")
	public SupplierResponseDto updateSupplier(final UUID id, final SupplierRequestDto requestDto) {
		final Supplier existingSupplier = supplierRepository.findById(id)
				.orElseThrow(() -> new AppException(SUPPLIER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		if (requestDto.email() != null && !requestDto.email().equalsIgnoreCase(existingSupplier.getEmail())) {
			if (supplierRepository.existsByEmailIgnoreCase(requestDto.email())) {
				throw new AppException(DUPLICATE_EMAIL.formatted(requestDto.email()), HttpStatus.CONFLICT);
			}
		}

		supplierMapper.updateEntityFromDto(requestDto, existingSupplier);
		final Supplier updatedSupplier = supplierRepository.save(existingSupplier);
		log.info("Proveedor actualizado: id={}, name={}", updatedSupplier.getId(), updatedSupplier.getName());
		return supplierMapper.toResponseDto(updatedSupplier);
	}

	@Transactional
	@CacheEvict(value = "suppliers", key = "#id")
	public void deleteSupplier(final UUID id) {
		final Supplier supplier = supplierRepository.findById(id)
				.orElseThrow(() -> new AppException(SUPPLIER_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		supplierRepository.delete(supplier);
		log.info("Proveedor eliminado: id={}", id);
	}
}
