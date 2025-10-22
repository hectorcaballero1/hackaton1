package org.example.hackaton01.sale.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackaton01.sale.Sale;
import org.example.hackaton01.sale.dto.SalesAggregates;
import org.example.hackaton01.sale.infrastructure.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesAggregationService {

    private final SaleRepository saleRepository;

    /**
     * Calcula agregados de ventas para un rango de fechas y opcionalmente una sucursal
     */
    public SalesAggregates calculateAggregates(LocalDateTime from, LocalDateTime to, String branch) {
        log.debug("Calculando agregados desde {} hasta {} para branch: {}", from, to, branch);

        // Obtener ventas del rango
        List<Sale> sales;
        if (branch != null) {
            sales = saleRepository.findByBranchAndSoldAtBetween(branch, from, to);
        } else {
            sales = saleRepository.findBySoldAtBetween(from, to);
        }

        if (sales.isEmpty()) {
            log.warn("No se encontraron ventas en el rango especificado");
            return new SalesAggregates(0, 0.0, null, null);
        }

        // Calcular totalUnits
        Integer totalUnits = sales.stream()
                .mapToInt(Sale::getUnits)
                .sum();

        // Calcular totalRevenue
        Double totalRevenue = sales.stream()
                .map(sale -> sale.getPrice().multiply(BigDecimal.valueOf(sale.getUnits())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        // Calcular topSku (el más vendido por unidades)
        String topSku = sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getSku,
                        Collectors.summingInt(Sale::getUnits)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Calcular topBranch (sucursal con más unidades vendidas)
        String topBranch = sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getBranch,
                        Collectors.summingInt(Sale::getUnits)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        log.info("Agregados calculados: {} unidades, ${} revenue, topSku: {}, topBranch: {}",
                totalUnits, totalRevenue, topSku, topBranch);

        return new SalesAggregates(totalUnits, totalRevenue, topSku, topBranch);
    }

    /**
     * Calcula agregados para la última semana automáticamente
     */
    public SalesAggregates calculateLastWeekAggregates(String branch) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusDays(7);
        return calculateAggregates(from, to, branch);
    }
}
