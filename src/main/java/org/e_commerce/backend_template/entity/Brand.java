package org.e_commerce.backend_template.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Brand extends BaseEntity {

	@Column(name = "name", nullable = false, unique = true, length = 150)
	@EqualsAndHashCode.Include
	@ToString.Include
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Builder.Default
	@Column(name = "active", nullable = false)
	@ToString.Include
	private Boolean active = true;

	@Builder.Default
	@OneToMany(mappedBy = "brand", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = false)
	private List<Product> products = new ArrayList<>();
}