package org.example.hackaton01.sale.saleagregation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SalesAggregatesResponse {
    private int totalUnits;
    private BigDecimal totalRevenue;
    private String topSku;
    private String topBranch;

}