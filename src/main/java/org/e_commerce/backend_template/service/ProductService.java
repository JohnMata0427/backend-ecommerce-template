package org.e_commerce.backend_template.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.e_commerce.backend_template.dto.ProductImageDto;
import org.e_commerce.backend_template.dto.ProductRequestDto;
import org.e_commerce.backend_template.dto.ProductResponseDto;
import org.e_commerce.backend_template.entity.Brand;
import org.e_commerce.backend_template.entity.LaptopModel;
import org.e_commerce.backend_template.entity.Product;
import org.e_commerce.backend_template.entity.ProductImage;
import org.e_commerce.backend_template.entity.Subcategory;
import org.e_commerce.backend_template.entity.Supplier;
import org.e_commerce.backend_template.exception.AppException;
import org.e_commerce.backend_template.mapper.ProductImageMapper;
import org.e_commerce.backend_template.mapper.ProductMapper;
import org.e_commerce.backend_template.repository.BrandRepository;
import org.e_commerce.backend_template.repository.LaptopModelRepository;
import org.e_commerce.backend_template.repository.ProductRepository;
import org.e_commerce.backend_template.repository.SubcategoryRepository;
import org.e_commerce.backend_template.repository.SupplierRepository;
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

	private static final String PRODUCT_NOT_FOUND = "Producto/Repuesto no encontrado con id: %s";
	private static final String PRODUCT_NOT_FOUND_SKU = "Producto/Repuesto no encontrado con SKU: %s";
	private static final String PRODUCT_NOT_FOUND_PART = "Producto/Repuesto no encontrado con Part Number: %s";
	private static final String DUPLICATE_SKU = "Ya existe un producto con el SKU: %s";
	private static final String SUBCATEGORY_NOT_FOUND = "Subcategoría no encontrada con id: %s";
	private static final String SUPPLIER_NOT_FOUND = "Proveedor no encontrado con id: %s";
	private static final String BRAND_NOT_FOUND = "Marca no encontrada con id: %s";
	private static final String CLOUDINARY_FOLDER = "laptop_parts";

	private final ProductRepository productRepository;
	private final SubcategoryRepository subcategoryRepository;
	private final SupplierRepository supplierRepository;
	private final BrandRepository brandRepository;
	private final LaptopModelRepository laptopModelRepository;
	private final ProductMapper productMapper;
	private final ProductImageMapper productImageMapper;
	private final CloudinaryService cloudinaryService;

	@Transactional
	public ProductResponseDto createProduct(final ProductRequestDto requestDto, final List<MultipartFile> images) {
		if (productRepository.existsBySku(requestDto.sku())) {
			throw new AppException(DUPLICATE_SKU.formatted(requestDto.sku()), HttpStatus.CONFLICT);
		}

		final Product product = productMapper.toEntity(requestDto);

		// Regla de negocio de repuestos: si es componente eléctrico y no se especificó garantía, default 3 meses
		if (Boolean.TRUE.equals(requestDto.isElectrical()) && (requestDto.warrantyMonths() == null || requestDto.warrantyMonths() == 0)) {
			product.setWarrantyMonths(3);
		}

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

		if (requestDto.compatibleModelIds() != null && !requestDto.compatibleModelIds().isEmpty()) {
			final List<LaptopModel> models = laptopModelRepository.findAllById(requestDto.compatibleModelIds());
			product.setCompatibleModels(new HashSet<>(models));
		}

		// Manejo de múltiples imágenes para repuestos de laptops (conectores, vista general, pines)
		if (images != null && !images.isEmpty()) {
			int order = 0;
			for (final MultipartFile file : images) {
				if (file != null && !file.isEmpty()) {
					final Map<String, String> uploadResult = cloudinaryService.uploadImage(file, CLOUDINARY_FOLDER);
					final boolean isFirst = (order == 0);
					final ProductImage pImage = ProductImage.builder()
							.imageUrl(uploadResult.get("secure_url"))
							.imagePublicId(uploadResult.get("public_id"))
							.isPrimary(isFirst)
							.sortOrder(order)
							.build();
					product.addImage(pImage);

					if (isFirst) {
						product.setImageUrl(uploadResult.get("secure_url"));
						product.setImagePublicId(uploadResult.get("public_id"));
					}
					order++;
				}
			}
		}

		final Product savedProduct = productRepository.save(product);
		log.info("Repuesto registrado en inventario: id={}, sku={}, partNumber={}", 
				savedProduct.getId(), savedProduct.getSku(), savedProduct.getPartNumber());
		return productMapper.toResponseDto(savedProduct);
	}

	public ProductResponseDto getProductById(final UUID id) {
		final Product product = productRepository.findById(id)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
		return productMapper.toResponseDto(product);
	}

	public ProductResponseDto getProductBySku(final String sku) {
		final Product product = productRepository.findBySku(sku)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND_SKU.formatted(sku), HttpStatus.NOT_FOUND));
		return productMapper.toResponseDto(product);
	}

	public ProductResponseDto getProductByPartNumber(final String partNumber) {
		final Product product = productRepository.findByPartNumberIgnoreCase(partNumber)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND_PART.formatted(partNumber), HttpStatus.NOT_FOUND));
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

	public Page<ProductResponseDto> getLowStockAlerts(final Pageable pageable) {
		return productRepository.findLowStockProducts(pageable)
				.map(productMapper::toResponseDto);
	}

	public Page<ProductResponseDto> getProductsByCompatibleModel(final UUID laptopModelId, final Pageable pageable) {
		return productRepository.findByCompatibleModelId(laptopModelId, pageable)
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

	public Page<ProductResponseDto> searchProductsByPartNumber(final String partNumber, final Pageable pageable) {
		return productRepository.findByPartNumberContainingIgnoreCase(partNumber, pageable)
				.map(productMapper::toResponseDto);
	}

	@Transactional
	public ProductResponseDto updateProduct(final UUID id, final ProductRequestDto requestDto) {
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

		if (requestDto.compatibleModelIds() != null) {
			final List<LaptopModel> models = laptopModelRepository.findAllById(requestDto.compatibleModelIds());
			existingProduct.setCompatibleModels(new HashSet<>(models));
		}

		final Product updatedProduct = productRepository.save(existingProduct);
		log.info("Repuesto actualizado: id={}, sku={}", updatedProduct.getId(), updatedProduct.getSku());
		return productMapper.toResponseDto(updatedProduct);
	}

	@Transactional
	public ProductImageDto addImageToProduct(final UUID productId, final MultipartFile file, final boolean isPrimary) {
		final Product product = productRepository.findById(productId)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(productId), HttpStatus.NOT_FOUND));

		final Map<String, String> uploadResult = cloudinaryService.uploadImage(file, CLOUDINARY_FOLDER);
		final int order = product.getImages().size();

		final ProductImage image = ProductImage.builder()
				.imageUrl(uploadResult.get("secure_url"))
				.imagePublicId(uploadResult.get("public_id"))
				.isPrimary(isPrimary || order == 0)
				.sortOrder(order)
				.build();

		product.addImage(image);

		if (Boolean.TRUE.equals(image.getIsPrimary())) {
			product.getImages().forEach(img -> {
				if (!img.equals(image)) {
					img.setIsPrimary(false);
				}
			});
			product.setImageUrl(image.getImageUrl());
			product.setImagePublicId(image.getImagePublicId());
		}

		productRepository.save(product);
		return productImageMapper.toDto(image);
	}

	@Transactional
	public void removeImageFromProduct(final UUID productId, final UUID imageId) {
		final Product product = productRepository.findById(productId)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(productId), HttpStatus.NOT_FOUND));

		final ProductImage imageToRemove = product.getImages().stream()
				.filter(img -> img.getId().equals(imageId))
				.findFirst()
				.orElseThrow(() -> new AppException("Imagen no encontrada con id: " + imageId, HttpStatus.NOT_FOUND));

		cloudinaryService.deleteImage(imageToRemove.getImagePublicId());
		product.removeImage(imageToRemove);
		productRepository.save(product);
	}

	@Transactional
	public void deleteProduct(final UUID id) {
		final Product product = productRepository.findById(id)
				.orElseThrow(() -> new AppException(PRODUCT_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));

		for (final ProductImage img : product.getImages()) {
			if (img.getImagePublicId() != null) {
				cloudinaryService.deleteImage(img.getImagePublicId());
			}
		}

		if (product.getImagePublicId() != null) {
			cloudinaryService.deleteImage(product.getImagePublicId());
		}

		productRepository.delete(product);
		log.info("Repuesto eliminado del inventario: id={}", id);
	}
}
