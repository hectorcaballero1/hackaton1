package org.example.hackaton01.sale.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleUpdateRequest {

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede tener más de 50 caracteres")
    private String sku;

    @NotNull(message = "Las unidades son obligatorias")
    @Min(value = 1, message = "Las unidades deben ser al menos 1")
    private Integer units;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotBlank(message = "La sucursal es obligatoria")
    @Size(max = 100, message = "La sucursal no puede tener más de 100 caracteres")
    private String branch;

    @NotNull(message = "La fecha de venta es obligatoria")
    private LocalDateTime soldAt;
}
