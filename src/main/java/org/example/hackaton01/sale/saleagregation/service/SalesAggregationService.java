package org.example.hackaton01.sale.saleagregation.service;

import org.example.hackaton01.sale.domain.Sale;
import org.example.hackaton01.sale.saleagregation.dto.SalesAggregatesResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SalesAggregationService {

    public SalesAggregatesResponse calculateAggregates(List<Sale> sales) {
        AggregationData data = sales.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        this::computeAllMetrics
                ));


        return SalesAggregatesResponse.builder()
                .totalUnits(data.totalUnits())
                .totalRevenue(data.totalRevenue())
                .topSku(data.topSku())
                .topBranch(data.topBranch())
                .build();
    }

    private AggregationData computeAllMetrics(List<Sale> sales) {
        Map<String, Integer> skuAggregates = aggregateBy(sales, Sale::getSku);
        Map<String, Integer> branchAggregates = aggregateBy(sales, Sale::getBranch);

        return new AggregationData(
                sales.stream().mapToInt(Sale::getUnits).sum(),
                calculateTotalRevenue(sales),
                findMaxKey(skuAggregates),
                findMaxKey(branchAggregates)
        );
    }

    private Map<String, Integer> aggregateBy(List<Sale> sales, Function<Sale, String> classifier) {
        return sales.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        Collectors.summingInt(Sale::getUnits)
                ));
    }

    private BigDecimal calculateTotalRevenue(List<Sale> sales) {
        return sales.stream()
                .map(sale -> sale.getPrice().multiply(BigDecimal.valueOf(sale.getUnits())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String findMaxKey(Map<String, Integer> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private record AggregationData(
            int totalUnits,
            BigDecimal totalRevenue,
            String topSku,
            String topBranch
    ) {}
}