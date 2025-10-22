package org.example.hackaton01.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private String id;
    private String sku;
    private Integer units;
    private BigDecimal price;
    private String branch;
    private LocalDateTime soldAt;
    private String createdBy;
    private LocalDateTime createdAt;
}
