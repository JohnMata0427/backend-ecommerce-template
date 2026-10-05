package org.e_commerce.backend_template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LaptopModelRequestDto(
    @NotBlank(message = "La marca de la laptop es obligatoria")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    String brandName,

    @Size(max = 100, message = "La serie no puede exceder 100 caracteres")
    String series,

    @NotBlank(message = "El número o código de modelo es obligatorio")
    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    String modelNumber,

    String notes) {
}
