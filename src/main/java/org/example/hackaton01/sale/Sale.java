package org.example.hackaton01.sale;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(generator = "sale-id-generator")
    @GenericGenerator(
        name = "sale-id-generator",
        strategy = "org.example.hackaton01.util.SaleIdGenerator"
    )
    @Column(length = 20)
    private String id;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede tener más de 50 caracteres")
    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @NotNull(message = "Las unidades son obligatorias")
    @Min(value = 1, message = "Las unidades deben ser al menos 1")
    @Column(name = "units", nullable = false)
    private Integer units;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "La sucursal es obligatoria")
    @Size(max = 100, message = "La sucursal no puede tener más de 100 caracteres")
    @Column(name = "branch", nullable = false, length = 100)
    private String branch;

    @NotNull(message = "La fecha de venta es obligatoria")
    @Column(name = "sold_at", nullable = false)
    private LocalDateTime soldAt;

    @NotBlank(message = "El usuario creador es obligatorio")
    @Size(max = 50, message = "El username no puede tener más de 50 caracteres")
    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
