package org.e_commerce.backend_template.controller;

import java.util.UUID;

import org.e_commerce.backend_template.dto.BrandRequestDto;
import org.e_commerce.backend_template.dto.BrandResponseDto;
import org.e_commerce.backend_template.service.BrandService;
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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

	private final BrandService brandService;

	@PostMapping
	public ResponseEntity<BrandResponseDto> createBrand(
			@RequestBody @Valid final BrandRequestDto requestDto) {
		final BrandResponseDto created = brandService.createBrand(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BrandResponseDto> getBrandById(@PathVariable final UUID id) {
		return ResponseEntity.ok(brandService.getBrandById(id));
	}

	@GetMapping
	public ResponseEntity<Page<BrandResponseDto>> getAllBrands(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(brandService.getAllBrands(pageable));
	}

	@GetMapping("/active")
	public ResponseEntity<Page<BrandResponseDto>> getActiveBrands(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(brandService.getActiveBrands(pageable));
	}

	@GetMapping("/search")
	public ResponseEntity<Page<BrandResponseDto>> searchBrands(
			@RequestParam final String name,
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(brandService.searchBrandsByName(name, pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<BrandResponseDto> updateBrand(
			@PathVariable final UUID id,
			@RequestBody @Valid final BrandRequestDto requestDto) {
		return ResponseEntity.ok(brandService.updateBrand(id, requestDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBrand(@PathVariable final UUID id) {
		brandService.deleteBrand(id);
		return ResponseEntity.noContent().build();
	}
}