package org.e_commerce.backend_template.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

  @Column(name = "cost_price", precision = 12, scale = 2)
  private BigDecimal costPrice;

  @Column(name = "stock", nullable = false)
  @ToString.Include
  private Integer stock;

  @Builder.Default
  @Column(name = "min_stock_alert", nullable = false)
  private Integer minStockAlert = 2;

  @Column(name = "sku", nullable = false, unique = true, length = 100)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String sku;

  @Column(name = "part_number", length = 100)
  @ToString.Include
  private String partNumber;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "condition", nullable = false, length = 30)
  private ProductCondition condition = ProductCondition.NEW;

  @Column(name = "location", length = 100)
  private String location;

  @Builder.Default
  @Column(name = "is_electrical", nullable = false)
  private Boolean isElectrical = false;

  @Builder.Default
  @Column(name = "warranty_months")
  private Integer warrantyMonths = 0;

  @Column(name = "compatibility_notes", columnDefinition = "TEXT")
  private String compatibilityNotes;

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
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  private List<ProductImage> images = new ArrayList<>();

  @Builder.Default
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_laptop_compatibilities",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "laptop_model_id")
  )
  private Set<LaptopModel> compatibleModels = new HashSet<>();

  @Builder.Default
  @Column(name = "active", nullable = false)
  @ToString.Include
  private Boolean active = true;

  public void addImage(final ProductImage image) {
    images.add(image);
    image.setProduct(this);
  }

  public void removeImage(final ProductImage image) {
    images.remove(image);
    image.setProduct(null);
  }
}
