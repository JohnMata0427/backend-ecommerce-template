package org.e_commerce.backend_template.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Product extends BaseEntity {

  @Column(name = "name", nullable = false, length = 255)
  @ToString.Include
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "price", nullable = false, precision = 12, scale = 2)
  @ToString.Include
  private BigDecimal price;

  @Column(name = "stock", nullable = false)
  @ToString.Include
  private Integer stock;

  @Column(name = "sku", nullable = false, unique = true, length = 100)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String sku;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subcategory_id")
  private Subcategory subcategory;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id")
  private Supplier supplier;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "brand_id")
  private Brand brand;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Column(name = "image_public_id", length = 255)
  private String imagePublicId;

  @Builder.Default
  @Column(name = "active", nullable = false)
  @ToString.Include
  private Boolean active = true;
}
