package org.e_commerce.backend_template.service;

import java.util.UUID;

import org.e_commerce.backend_template.dto.SubcategoryRequestDto;
import org.e_commerce.backend_template.dto.SubcategoryResponseDto;
import org.e_commerce.backend_template.entity.Category;
import org.e_commerce.backend_template.entity.Subcategory;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.SubcategoryMapper;
import org.e_commerce.backend_template.repository.CategoryRepository;
import org.e_commerce.backend_template.repository.SubcategoryRepository;
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
public class SubcategoryService {

	private static final String SUBCATEGORY_NOT_FOUND = "Subcategoría no encontrada con id: %s";
	private static final String CATEGORY_NOT_FOUND = "Categoría no encontrada con id: %s";
	private static final String DUPLICATE_NAME = "Ya existe una subcategoría con el nombre '%s' en la categoría indicada";

	private final SubcategoryRepository subcategoryRepository;
	private final CategoryRepository categoryRepository;
	private final SubcategoryMapper subcategoryMapper;

	@Transactional
	public SubcategoryResponseDto createSubcategory(final SubcategoryRequestDto requestDto) {
		final Category category = categoryRepository.findById(requestDto.categoryId())
				.orElseThrow(
						() -> new AppException(CATEGORY_NOT_FOUND.formatted(requestDto.categoryId()), HttpStatus.NOT_FOUND));

		if (subcategoryRepository.existsByNameIgnoreCaseAndCategoryId(requestDto.name(), requestDto.categoryId())) {
			throw new AppException(DUPLICATE_NAME.formatted(requestDto.name()), HttpStatus.CONFLICT);
		}

		final Subcategory subcategory = subcategoryMapper.toEntity(requestDto);
		subcategory.setCategory(category);

		final Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
		log.info("Subcategoría creada: id={}, name={}, categoryId={}",
				savedSubcategory.getId(), savedSubcategory.getName(), category.getId());
		return subcategoryMapper.toResponseDto(savedSubcategory);
	}

	@Cacheable(value = "subcategories", key = "#id")
	public SubcategoryResponseDto getSubcategoryById(final UUID id) {
		final Subcategory subcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new AppException(SUBCATEGORY_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		return subcategoryMapper.toResponseDto(subcategory);
	}

	public Page<SubcategoryResponseDto> getAllSubcategories(final Pageable pageable) {
		return subcategoryRepository.findAll(pageable)
				.map(subcategoryMapper::toResponseDto);
	}

	public Page<SubcategoryResponseDto> getActiveSubcategories(final Pageable pageable) {
		return subcategoryRepository.findByActiveTrue(pageable)
				.map(subcategoryMapper::toResponseDto);
	}

	public Page<SubcategoryResponseDto> getSubcategoriesByCategoryId(final UUID categoryId, final Pageable pageable) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new AppException(CATEGORY_NOT_FOUND.formatted(categoryId), HttpStatus.NOT_FOUND);
		}
		return subcategoryRepository.findByCategoryId(categoryId, pageable)
				.map(subcategoryMapper::toResponseDto);
	}

	public Page<SubcategoryResponseDto> searchSubcategoriesByName(final String name, final Pageable pageable) {
		return subcategoryRepository.findByNameContainingIgnoreCase(name, pageable)
				.map(subcategoryMapper::toResponseDto);
	}

	@Transactional
	@CachePut(value = "subcategories", key = "#id")
	public SubcategoryResponseDto updateSubcategory(final UUID id, final SubcategoryRequestDto requestDto) {
		final Subcategory existingSubcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new AppException(SUBCATEGORY_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		if (requestDto.categoryId() != null
				&& !requestDto.categoryId().equals(existingSubcategory.getCategory().getId())) {
			final Category newCategory = categoryRepository.findById(requestDto.categoryId())
					.orElseThrow(
							() -> new AppException(CATEGORY_NOT_FOUND.formatted(requestDto.categoryId()), HttpStatus.NOT_FOUND));
			existingSubcategory.setCategory(newCategory);
		}

		final UUID categoryId = existingSubcategory.getCategory().getId();
		if (requestDto.name() != null && !requestDto.name().equalsIgnoreCase(existingSubcategory.getName())) {
			if (subcategoryRepository.existsByNameIgnoreCaseAndCategoryId(requestDto.name(), categoryId)) {
				throw new AppException(DUPLICATE_NAME.formatted(requestDto.name()), HttpStatus.CONFLICT);
			}
		}

		subcategoryMapper.updateEntityFromDto(requestDto, existingSubcategory);
		final Subcategory updatedSubcategory = subcategoryRepository.save(existingSubcategory);
		log.info("Subcategoría actualizada: id={}, name={}", updatedSubcategory.getId(), updatedSubcategory.getName());
		return subcategoryMapper.toResponseDto(updatedSubcategory);
	}

	@Transactional
	@CacheEvict(value = "subcategories", key = "#id")
	public void deleteSubcategory(final UUID id) {
		final Subcategory subcategory = subcategoryRepository.findById(id)
				.orElseThrow(() -> new AppException(SUBCATEGORY_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		subcategoryRepository.delete(subcategory);
		log.info("Subcategoría eliminada: id={}", id);
	}
}
