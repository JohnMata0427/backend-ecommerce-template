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
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Supplier extends BaseEntity {

	@Column(name = "name", nullable = false, length = 255)
	@ToString.Include
	private String name;

	@Column(name = "contact_name", length = 255)
	private String contactName;

	@Column(name = "email", nullable = false, unique = true, length = 255)
	@EqualsAndHashCode.Include
	@ToString.Include
	private String email;

	@Column(name = "phone", length = 50)
	private String phone;

	@Column(name = "address", columnDefinition = "TEXT")
	private String address;

	@Builder.Default
	@Column(name = "active", nullable = false)
	@ToString.Include
	private Boolean active = true;

	@Builder.Default
	@OneToMany(mappedBy = "supplier", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = false)
	private List<Product> products = new ArrayList<>();
}
