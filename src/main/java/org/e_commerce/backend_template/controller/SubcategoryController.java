package org.e_commerce.backend_template.controller;

import java.util.UUID;

import org.e_commerce.backend_template.dto.SubcategoryRequestDto;
import org.e_commerce.backend_template.dto.SubcategoryResponseDto;
import org.e_commerce.backend_template.service.SubcategoryService;
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
@RequestMapping("/api/v1/subcategories")
@RequiredArgsConstructor
public class SubcategoryController {

	private final SubcategoryService subcategoryService;

	@PostMapping
	public ResponseEntity<SubcategoryResponseDto> createSubcategory(
			@RequestBody @Valid final SubcategoryRequestDto requestDto) {
		final SubcategoryResponseDto created = subcategoryService.createSubcategory(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SubcategoryResponseDto> getSubcategoryById(@PathVariable final UUID id) {
		return ResponseEntity.ok(subcategoryService.getSubcategoryById(id));
	}

	@GetMapping
	public ResponseEntity<Page<SubcategoryResponseDto>> getAllSubcategories(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(subcategoryService.getAllSubcategories(pageable));
	}

	@GetMapping("/active")
	public ResponseEntity<Page<SubcategoryResponseDto>> getActiveSubcategories(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(subcategoryService.getActiveSubcategories(pageable));
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<Page<SubcategoryResponseDto>> getSubcategoriesByCategory(
			@PathVariable final UUID categoryId,
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(subcategoryService.getSubcategoriesByCategoryId(categoryId, pageable));
	}

	@GetMapping("/search")
	public ResponseEntity<Page<SubcategoryResponseDto>> searchSubcategories(
			@RequestParam final String name,
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(subcategoryService.searchSubcategoriesByName(name, pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SubcategoryResponseDto> updateSubcategory(
			@PathVariable final UUID id,
			@RequestBody @Valid final SubcategoryRequestDto requestDto) {
		return ResponseEntity.ok(subcategoryService.updateSubcategory(id, requestDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSubcategory(@PathVariable final UUID id) {
		subcategoryService.deleteSubcategory(id);
		return ResponseEntity.noContent().build();
	}
}
