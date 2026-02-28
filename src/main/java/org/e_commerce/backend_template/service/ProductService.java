package org.e_commerce.backend_template.service;

import java.util.Map;
import java.util.UUID;

import org.e_commerce.backend_template.dto.ProductRequestDto;
import org.e_commerce.backend_template.dto.ProductResponseDto;
import org.e_commerce.backend_template.entity.Brand;
import org.e_commerce.backend_template.entity.Product;
import org.e_commerce.backend_template.entity.Subcategory;
import org.e_commerce.backend_template.entity.Supplier;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.ProductMapper;
import org.e_commerce.backend_template.repository.BrandRepository;
import org.e_commerce.backend_template.repository.ProductRepository;
import org.e_commerce.backend_template.repository.SubcategoryRepository;
import org.e_commerce.backend_template.repository.SupplierRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private static final String PRODUCT_NOT_FOUND = "Producto no encontrado con id: %s";
	private static final String PRODUCT_NOT_FOUND_SKU = "Producto no encontrado con SKU: %s";
	private static final String DUPLICATE_SKU = "Ya existe un producto con el SKU: %s";
	private static final String SUBCATEGORY_NOT_FOUND = "Subcategoría no encontrada con id: %s";
	private static final String SUPPLIER_NOT_FOUND = "Proveedor no encontrado con id: %s";
	private static final String BRAND_NOT_FOUND = "Marca no encontrada con id: %s";
	private static final String CLOUDINARY_FOLDER = "products";

	private final ProductRepository productRepository;
	private final SubcategoryRepository subcategoryRepository;
	private final SupplierRepository supplierRepository;
	private final BrandRepository brandRepository;
	private final ProductMapper productMapper;
	private final CloudinaryService cloudinaryService;

	@Transactional
	public ProductResponseDto createProduct(final ProductRequestDto requestDto, final MultipartFile image) {
		if (productRepository.existsBySku(requestDto.sku())) {
			throw new AppException(DUPLICATE_SKU.formatted(requestDto.sku()), HttpStatus.CONFLICT);
		}

		final Product product = productMapper.toEntity(requestDto);

		if (requestDto.subcategoryId() != null) {
			final Subcategory subcategory = subcategoryRepository.findById(requestDto.subcategoryId())
					.orElseThrow(() -> new AppException(
							SUBCATEGORY_NOT_FOUND.formatted(requestDto.subcategoryId()), HttpStatus.NOT_FOUND));
			product.setSubcategory(subcategory);
		}

		if (requestDto.supplierId() != null) {
			final Supplier supplier = supplierRepository.findById(requestDto.supplierId())
					.orElseThrow(
							() -> new AppException(SUPPLIER_NOT_FOUND.formatted(requestDto.supplierId()), HttpStatus.NOT_FOUND));
			product.setSupplier(supplier);
		}

		if (requestDto.brandId() != null) {
			final Brand brand = brandRepository.findById(requestDto.brandId())
					.orElseThrow(
							() -> new AppException(BRAND_NOT_FOUND.formatted(requestDto.brandId()), HttpStatus.NOT_FOUND));
			product.setBrand(brand);
		}

		if (image != null && !image.isEmpty()) {
			final Map<String, String> uploadResult = cloudinaryService.uploadImage(image, CLOUDINARY_FOLDER);
			product.setImageUrl(uploadResult.get("secure_url"));
			product.setImagePublicId(uploadResult.get("public_id"));
		}

		final Product savedProduct = productRepository.save(product);
		log.info("Producto creado: id={}, sku={}", savedProduct.getId(), savedProduct.getSku());
		return productMapper.toResponseDto(savedProduct);
	}

	@Cacheable(value = "products", key = "#id")
	public ProductResponseDto getProductById(final UUID id) {
		final Product product = productRepository.findById(id)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		return productMapper.toResponseDto(product);
	}

	@Cacheable(value = "products", key = "#sku")
	public ProductResponseDto getProductBySku(final String sku) {
		final Product product = productRepository.findBySku(sku)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND_SKU.formatted(sku), HttpStatus.NOT_FOUND));
		return productMapper.toResponseDto(product);
	}

	public Page<ProductResponseDto> getAllProducts(final Pageable pageable) {
		return productRepository.findAll(pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> getActiveProducts(final Pageable pageable) {
		return productRepository.findByActiveTrue(pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> getProductsBySubcategoryId(final UUID subcategoryId, final Pageable pageable) {
		return productRepository.findBySubcategoryId(subcategoryId, pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> getProductsBySupplierId(final UUID supplierId, final Pageable pageable) {
		return productRepository.findBySupplierId(supplierId, pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> getProductsByBrandId(final UUID brandId, final Pageable pageable) {
		return productRepository.findByBrandId(brandId, pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> getProductsByCategoryId(final UUID categoryId, final Pageable pageable) {
		return productRepository.findBySubcategory_Category_Id(categoryId, pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> searchProductsByName(final String name, final Pageable pageable) {
		return productRepository.findByNameContainingIgnoreCase(name, pageable)
				.map(productMapper::toResponseDto);
	}

	@Transactional
	@CachePut(value = "products", key = "#id")
	public ProductResponseDto updateProduct(final UUID id, final ProductRequestDto requestDto,
			final MultipartFile image) {
		final Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		if (requestDto.sku() != null && !requestDto.sku().equals(existingProduct.getSku())) {
			if (productRepository.existsBySku(requestDto.sku())) {
				throw new AppException(DUPLICATE_SKU.formatted(requestDto.sku()), HttpStatus.CONFLICT);
			}
		}

		productMapper.updateEntityFromDto(requestDto, existingProduct);

		if (requestDto.subcategoryId() != null) {
			final Subcategory subcategory = subcategoryRepository.findById(requestDto.subcategoryId())
					.orElseThrow(() -> new AppException(
							SUBCATEGORY_NOT_FOUND.formatted(requestDto.subcategoryId()), HttpStatus.NOT_FOUND));
			existingProduct.setSubcategory(subcategory);
		}

		if (requestDto.supplierId() != null) {
			final Supplier supplier = supplierRepository.findById(requestDto.supplierId())
					.orElseThrow(
							() -> new AppException(SUPPLIER_NOT_FOUND.formatted(requestDto.supplierId()), HttpStatus.NOT_FOUND));
			existingProduct.setSupplier(supplier);
		}

		if (requestDto.brandId() != null) {
			final Brand brand = brandRepository.findById(requestDto.brandId())
					.orElseThrow(
							() -> new AppException(BRAND_NOT_FOUND.formatted(requestDto.brandId()), HttpStatus.NOT_FOUND));
			existingProduct.setBrand(brand);
		}

		if (image != null && !image.isEmpty()) {
			if (existingProduct.getImagePublicId() != null) {
				cloudinaryService.deleteImage(existingProduct.getImagePublicId());
			}
			final Map<String, String> uploadResult = cloudinaryService.uploadImage(image, CLOUDINARY_FOLDER);
			existingProduct.setImageUrl(uploadResult.get("secure_url"));
			existingProduct.setImagePublicId(uploadResult.get("public_id"));
		}

		final Product updatedProduct = productRepository.save(existingProduct);
		log.info("Producto actualizado: id={}, sku={}", updatedProduct.getId(), updatedProduct.getSku());
		return productMapper.toResponseDto(updatedProduct);
	}

	@Transactional
	@CacheEvict(value = "products", key = "#id")
	public void deleteProduct(final UUID id) {
		final Product product = productRepository.findById(id)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		if (product.getImagePublicId() != null) {
			cloudinaryService.deleteImage(product.getImagePublicId());
		}

		productRepository.delete(product);
		log.info("Producto eliminado: id={}", id);
	}
}
