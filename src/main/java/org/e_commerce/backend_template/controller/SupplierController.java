package org.e_commerce.backend_template.controller;

import java.util.UUID;

import org.e_commerce.backend_template.dto.SupplierRequestDto;
import org.e_commerce.backend_template.dto.SupplierResponseDto;
import org.e_commerce.backend_template.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

	private final SupplierService supplierService;

	@PostMapping
	public ResponseEntity<SupplierResponseDto> createSupplier(
			@RequestBody @Valid final SupplierRequestDto requestDto) {
		final SupplierResponseDto created = supplierService.createSupplier(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable final UUID id) {
		return ResponseEntity.ok(supplierService.getSupplierById(id));
	}

	@GetMapping
	public ResponseEntity<Page<SupplierResponseDto>> getAllSuppliers(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
	}

	@GetMapping("/active")
	public ResponseEntity<Page<SupplierResponseDto>> getActiveSuppliers(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(supplierService.getActiveSuppliers(pageable));
	}

	@GetMapping("/search")
	public ResponseEntity<Page<SupplierResponseDto>> searchSuppliers(
			@RequestParam final String name,
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(supplierService.searchSuppliersByName(name, pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SupplierResponseDto> updateSupplier(
			@PathVariable final UUID id,
			@RequestBody @Valid final SupplierRequestDto requestDto) {
		return ResponseEntity.ok(supplierService.updateSupplier(id, requestDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSupplier(@PathVariable final UUID id) {
		supplierService.deleteSupplier(id);
		return ResponseEntity.noContent().build();
	}
}
