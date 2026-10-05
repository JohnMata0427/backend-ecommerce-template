package org.e_commerce.backend_template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "laptop_models",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"brand_name", "series", "model_number"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class LaptopModel extends BaseEntity {

    @Column(name = "brand_name", nullable = false, length = 100)
    @ToString.Include
    private String brandName;

    @Column(name = "series", length = 100)
    @ToString.Include
    private String series;

    @Column(name = "model_number", nullable = false, length = 100)
    @ToString.Include
    private String modelNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
