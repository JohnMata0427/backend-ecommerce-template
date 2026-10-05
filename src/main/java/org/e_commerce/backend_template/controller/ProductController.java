package org.e_commerce.backend_template.controller;

import java.util.List;
import java.util.UUID;

import org.e_commerce.backend_template.dto.ProductImageDto;
import org.e_commerce.backend_template.dto.ProductRequestDto;
import org.e_commerce.backend_template.dto.ProductResponseDto;
import org.e_commerce.backend_template.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductResponseDto> createProduct(
      @RequestPart("product") @Valid final ProductRequestDto requestDto,
      @RequestPart(value = "images", required = false) final List<MultipartFile> images) {
    final ProductResponseDto created = productService.createProduct(requestDto, images);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponseDto> getProductById(@PathVariable final UUID id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @GetMapping("/sku/{sku}")
  public ResponseEntity<ProductResponseDto> getProductBySku(@PathVariable final String sku) {
    return ResponseEntity.ok(productService.getProductBySku(sku));
  }

  @GetMapping("/part-number/{partNumber}")
  public ResponseEntity<ProductResponseDto> getProductByPartNumber(@PathVariable final String partNumber) {
    return ResponseEntity.ok(productService.getProductByPartNumber(partNumber));
  }

  @GetMapping
  public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
      @PageableDefault(size = 20, sort = "name") final Pageable pageable) {
    return ResponseEntity.ok(productService.getAllProducts(pageable));
  }

  @GetMapping("/active")
  public ResponseEntity<Page<ProductResponseDto>> getActiveProducts(
      @PageableDefault(size = 20, sort = "name") final Pageable pageable) {
    return ResponseEntity.ok(productService.getActiveProducts(pageable));
  }

  @GetMapping("/low-stock")
  public ResponseEntity<Page<ProductResponseDto>> getLowStockAlerts(
      @PageableDefault(size = 20) final Pageable pageable) {
    return ResponseEntity.ok(productService.getLowStockAlerts(pageable));
  }

  @GetMapping("/compatible-model/{modelId}")
  public ResponseEntity<Page<ProductResponseDto>> getProductsByCompatibleModel(
      @PathVariable final UUID modelId,
      @PageableDefault(size = 20) final Pageable pageable) {
    return ResponseEntity.ok(productService.getProductsByCompatibleModel(modelId, pageable));
  }

  @GetMapping("/subcategory/{subcategoryId}")
  public ResponseEntity<Page<ProductResponseDto>> getProductsBySubcategoryId(
      @PathVariable final UUID subcategoryId,
      @PageableDefault(size = 20, sort = "name") final Pageable pageable) {
    return ResponseEntity.ok(productService.getProductsBySubcategoryId(subcategoryId, pageable));
  }

  @GetMapping("/supplier/{supplierId}")
  public ResponseEntity<Page<ProductResponseDto>> getProductsBySupplierId(
      @PathVariable final UUID supplierId,
      @PageableDefault(size = 20, sort = "name") final Pageable pageable) {
    return ResponseEntity.ok(productService.getProductsBySupplierId(supplierId, pageable));
  }

  @GetMapping("/brand/{brandId}")
  public ResponseEntity<Page<ProductResponseDto>> getProductsByBrandId(
      @PathVariable final UUID brandId,
      @PageableDefault(size = 20, sort = "name") final Pageable pageable) {
    return ResponseEntity.ok(productService.getProductsByBrandId(brandId, pageable));
  }

  @GetMapping("/category/{categoryId}")
  public ResponseEntity<Page<ProductResponseDto>> getProductsByCategoryId(
      @PathVariable final UUID categoryId,
      @PageableDefault(size = 20, sort = "name") final Pageable pageable) {
    return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId, pageable));
  }

  @GetMapping("/search")
  public ResponseEntity<Page<ProductResponseDto>> searchProductsByName(
      @RequestParam("name") final String name,
      @PageableDefault(size = 20) final Pageable pageable) {
    return ResponseEntity.ok(productService.searchProductsByName(name, pageable));
  }

  @GetMapping("/search/part-number")
  public ResponseEntity<Page<ProductResponseDto>> searchProductsByPartNumber(
      @RequestParam("partNumber") final String partNumber,
      @PageableDefault(size = 20) final Pageable pageable) {
    return ResponseEntity.ok(productService.searchProductsByPartNumber(partNumber, pageable));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDto> updateProduct(
      @PathVariable final UUID id,
      @RequestBody @Valid final ProductRequestDto requestDto) {
    return ResponseEntity.ok(productService.updateProduct(id, requestDto));
  }

  @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductImageDto> addImage(
      @PathVariable final UUID id,
      @RequestPart("image") final MultipartFile image,
      @RequestParam(value = "isPrimary", defaultValue = "false") final boolean isPrimary) {
    return ResponseEntity.status(HttpStatus.CREATED).body(productService.addImageToProduct(id, image, isPrimary));
  }

  @DeleteMapping("/{id}/images/{imageId}")
  public ResponseEntity<Void> removeImage(
      @PathVariable final UUID id,
      @PathVariable final UUID imageId) {
    productService.removeImageFromProduct(id, imageId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable final UUID id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
