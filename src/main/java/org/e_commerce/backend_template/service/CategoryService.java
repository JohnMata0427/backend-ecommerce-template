package org.e_commerce.backend_template.service;

import java.util.UUID;

import org.e_commerce.backend_template.dto.CategoryRequestDto;
import org.e_commerce.backend_template.dto.CategoryResponseDto;
import org.e_commerce.backend_template.entity.Category;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.CategoryMapper;
import org.e_commerce.backend_template.repository.CategoryRepository;
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
public class CategoryService {

	private static final String CATEGORY_NOT_FOUND = "Categoría no encontrada con id: %s";
	private static final String DUPLICATE_NAME = "Ya existe una categoría con el nombre: %s";

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	@Transactional
	public CategoryResponseDto createCategory(final CategoryRequestDto requestDto) {
		if (categoryRepository.existsByNameIgnoreCase(requestDto.name())) {
			throw new AppException(DUPLICATE_NAME.formatted(requestDto.name()), HttpStatus.CONFLICT);
		}

		final Category category = categoryMapper.toEntity(requestDto);
		final Category savedCategory = categoryRepository.save(category);
		log.info("Categoría creada: id={}, name={}", savedCategory.getId(), savedCategory.getName());
		return categoryMapper.toResponseDto(savedCategory);
	}

	@Cacheable(value = "categories", key = "#id")
	public CategoryResponseDto getCategoryById(final UUID id) {
		final Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException(CATEGORY_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		return categoryMapper.toResponseDto(category);
	}

	public Page<CategoryResponseDto> getAllCategories(final Pageable pageable) {
		return categoryRepository.findAll(pageable)
				.map(categoryMapper::toResponseDto);
	}

	public Page<CategoryResponseDto> getActiveCategories(final Pageable pageable) {
		return categoryRepository.findByActiveTrue(pageable)
				.map(categoryMapper::toResponseDto);
	}

	public Page<CategoryResponseDto> searchCategoriesByName(final String name, final Pageable pageable) {
		return categoryRepository.findByNameContainingIgnoreCase(name, pageable)
				.map(categoryMapper::toResponseDto);
	}

	@Transactional
	@CachePut(value = "categories", key = "#id")
	public CategoryResponseDto updateCategory(final UUID id, final CategoryRequestDto requestDto) {
		final Category existingCategory = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException(CATEGORY_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		if (requestDto.name() != null && !requestDto.name().equalsIgnoreCase(existingCategory.getName())) {
			if (categoryRepository.existsByNameIgnoreCase(requestDto.name())) {
				throw new AppException(DUPLICATE_NAME.formatted(requestDto.name()), HttpStatus.CONFLICT);
			}
		}

		categoryMapper.updateEntityFromDto(requestDto, existingCategory);
		final Category updatedCategory = categoryRepository.save(existingCategory);
		log.info("Categoría actualizada: id={}, name={}", updatedCategory.getId(), updatedCategory.getName());
		return categoryMapper.toResponseDto(updatedCategory);
	}

	@Transactional
	@CacheEvict(value = "categories", key = "#id")
	public void deleteCategory(final UUID id) {
		final Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException(CATEGORY_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		categoryRepository.delete(category);
		log.info("Categoría eliminada: id={}", id);
	}
}
