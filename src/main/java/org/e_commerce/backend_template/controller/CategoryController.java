package org.e_commerce.backend_template.controller;

import java.util.UUID;

import org.e_commerce.backend_template.dto.CategoryRequestDto;
import org.e_commerce.backend_template.dto.CategoryResponseDto;
import org.e_commerce.backend_template.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryResponseDto> createCategory(
			@RequestBody @Valid final CategoryRequestDto requestDto) {
		final CategoryResponseDto created = categoryService.createCategory(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable final UUID id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	@GetMapping
	public ResponseEntity<Page<CategoryResponseDto>> getAllCategories(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(categoryService.getAllCategories(pageable));
	}

	@GetMapping("/active")
	public ResponseEntity<Page<CategoryResponseDto>> getActiveCategories(
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(categoryService.getActiveCategories(pageable));
	}

	@GetMapping("/search")
	public ResponseEntity<Page<CategoryResponseDto>> searchCategories(
			@RequestParam final String name,
			@PageableDefault(size = 20, sort = "name") final Pageable pageable) {
		return ResponseEntity.ok(categoryService.searchCategoriesByName(name, pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategory(
			@PathVariable final UUID id,
			@RequestBody @Valid final CategoryRequestDto requestDto) {
		return ResponseEntity.ok(categoryService.updateCategory(id, requestDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable final UUID id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
