package org.example.hackaton01.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesAggregates {
    private Integer totalUnits;
    private Double totalRevenue;
    private String topSku;
    private String topBranch;
}
