package org.example.hackaton01.sale.saleagregation.service;

import org.example.hackaton01.sale.domain.Sale;
import org.example.hackaton01.sale.saleagregation.dto.SalesAggregatesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para SalesAggregationService
 * Según README.md líneas 217-266
 */
@DisplayName("SalesAggregationService - Tests Unitarios")
class SalesAggregationServiceTest {

    private SalesAggregationService salesAggregationService;

    @BeforeEach
    void setUp() {
        salesAggregationService = new SalesAggregationService();
    }

    /**
     * Test 1: Test de agregados con datos válidos
     * Verifica que se calculen correctamente totalUnits, totalRevenue, topSku y topBranch
     */
    @Test
    @DisplayName("1. Debe calcular correctamente los agregados con datos válidos")
    void shouldCalculateCorrectAggregatesWithValidData() {
        // Given - Crear ventas de prueba
        List<Sale> mockSales = List.of(
                createSale(1L, "OREO_CLASSIC", 10, new BigDecimal("1.99"), "Miraflores"),
                createSale(2L, "OREO_DOUBLE", 5, new BigDecimal("2.49"), "San Isidro"),
                createSale(3L, "OREO_CLASSIC", 15, new BigDecimal("1.99"), "Miraflores")
        );

        // When - Calcular agregados
        SalesAggregatesResponse result = salesAggregationService.calculateAggregates(mockSales);

        // Then - Verificar resultados
        assertThat(result).isNotNull();

        // Verificar total de unidades: 10 + 5 + 15 = 30
        assertThat(result.getTotalUnits()).isEqualTo(30);

        // Verificar revenue total: (10 * 1.99) + (5 * 2.49) + (15 * 1.99) = 19.90 + 12.45 + 29.85 = 62.20
        assertThat(result.getTotalRevenue())
                .isEqualByComparingTo(new BigDecimal("62.20"));

        // Verificar SKU más vendido: OREO_CLASSIC (10 + 15 = 25 unidades)
        assertThat(result.getTopSku()).isEqualTo("OREO_CLASSIC");

        // Verificar sucursal líder: Miraflores (10 + 15 = 25 unidades)
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    /**
     * Test 2: Test con lista vacía
     * Verifica el comportamiento cuando no hay ventas
     */
    @Test
    @DisplayName("2. Debe manejar correctamente una lista vacía de ventas")
    void shouldHandleEmptyListCorrectly() {
        // Given - Lista vacía
        List<Sale> emptySales = new ArrayList<>();

        // When - Calcular agregados
        SalesAggregatesResponse result = salesAggregationService.calculateAggregates(emptySales);

        // Then - Verificar valores por defecto
        assertThat(result).isNotNull();
        assertThat(result.getTotalUnits()).isEqualTo(0);
        assertThat(result.getTotalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTopSku()).isEqualTo("N/A");
        assertThat(result.getTopBranch()).isEqualTo("N/A");
    }

    /**
     * Test 3: Test de filtrado por sucursal
     * Verifica que solo considere ventas de la sucursal especificada
     */
    @Test
    @DisplayName("3. Debe calcular agregados solo para la sucursal especificada")
    void shouldCalculateAggregatesForSpecificBranchOnly() {
        // Given - Ventas de diferentes sucursales
        List<Sale> allSales = List.of(
                createSale(1L, "OREO_CLASSIC", 20, new BigDecimal("1.99"), "Miraflores"),
                createSale(2L, "OREO_DOUBLE", 30, new BigDecimal("2.49"), "San Isidro"),
                createSale(3L, "OREO_THINS", 15, new BigDecimal("2.19"), "Miraflores"),
                createSale(4L, "OREO_GOLDEN", 25, new BigDecimal("3.49"), "San Isidro")
        );

        // Filtrar manualmente solo Miraflores (simula lo que haría el repositorio)
        List<Sale> mirafloреsSales = allSales.stream()
                .filter(sale -> "Miraflores".equals(sale.getBranch()))
                .toList();

        // When - Calcular agregados solo para Miraflores
        SalesAggregatesResponse result = salesAggregationService.calculateAggregates(mirafloреsSales);

        // Then - Verificar que solo cuenta ventas de Miraflores
        assertThat(result).isNotNull();

        // Total unidades: 20 + 15 = 35 (solo Miraflores)
        assertThat(result.getTotalUnits()).isEqualTo(35);

        // Revenue: (20 * 1.99) + (15 * 2.19) = 39.80 + 32.85 = 72.65
        assertThat(result.getTotalRevenue())
                .isEqualByComparingTo(new BigDecimal("72.65"));

        // Top branch debe ser Miraflores (única sucursal en el dataset filtrado)
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    /**
     * Test 4: Test de filtrado por fechas
     * Verifica que solo considere ventas dentro del rango de fechas
     */
    @Test
    @DisplayName("4. Debe calcular agregados solo para el rango de fechas especificado")
    void shouldCalculateAggregatesForSpecificDateRangeOnly() {
        // Given - Ventas en diferentes fechas
        LocalDateTime startDate = LocalDateTime.of(2025, 9, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 9, 3, 23, 59);

        List<Sale> allSales = List.of(
                createSaleWithDate(1L, "OREO_CLASSIC", 10, new BigDecimal("1.99"), "Miraflores",
                        LocalDateTime.of(2025, 9, 1, 10, 0)), // DENTRO del rango
                createSaleWithDate(2L, "OREO_DOUBLE", 20, new BigDecimal("2.49"), "San Isidro",
                        LocalDateTime.of(2025, 9, 2, 15, 0)), // DENTRO del rango
                createSaleWithDate(3L, "OREO_THINS", 30, new BigDecimal("2.19"), "Miraflores",
                        LocalDateTime.of(2025, 9, 5, 10, 0))  // FUERA del rango
        );

        // Filtrar manualmente por rango de fechas (simula lo que haría el repositorio)
        List<Sale> salesInRange = allSales.stream()
                .filter(sale -> !sale.getSoldAt().isBefore(startDate) && !sale.getSoldAt().isAfter(endDate))
                .toList();

        // When - Calcular agregados solo para el rango de fechas
        SalesAggregatesResponse result = salesAggregationService.calculateAggregates(salesInRange);

        // Then - Verificar que solo cuenta ventas dentro del rango
        assertThat(result).isNotNull();

        // Total unidades: 10 + 20 = 30 (sin la venta del 5 de septiembre)
        assertThat(result.getTotalUnits()).isEqualTo(30);

        // Revenue: (10 * 1.99) + (20 * 2.49) = 19.90 + 49.80 = 69.70
        assertThat(result.getTotalRevenue())
                .isEqualByComparingTo(new BigDecimal("69.70"));

        // Top SKU debe ser OREO_DOUBLE (20 unidades)
        assertThat(result.getTopSku()).isEqualTo("OREO_DOUBLE");
    }

    /**
     * Test 5: Test de cálculo de SKU top con empates
     * Verifica que identifique correctamente el SKU más vendido cuando hay empates
     */
    @Test
    @DisplayName("5. Debe identificar correctamente el SKU más vendido incluso con empates")
    void shouldIdentifyTopSkuCorrectlyWithTies() {
        // Given - Escenario con empate y un ganador claro
        List<Sale> mockSales = List.of(
                createSale(1L, "OREO_CLASSIC", 15, new BigDecimal("1.99"), "Miraflores"),
                createSale(2L, "OREO_DOUBLE", 15, new BigDecimal("2.49"), "San Isidro"),  // Empate con CLASSIC
                createSale(3L, "OREO_THINS", 25, new BigDecimal("2.19"), "Miraflores"),   // Ganador
                createSale(4L, "OREO_GOLDEN", 10, new BigDecimal("3.49"), "San Isidro")
        );

        // When - Calcular agregados
        SalesAggregatesResponse result = salesAggregationService.calculateAggregates(mockSales);

        // Then - Verificar resultados
        assertThat(result).isNotNull();

        // Total unidades: 15 + 15 + 25 + 10 = 65
        assertThat(result.getTotalUnits()).isEqualTo(65);

        // Top SKU debe ser OREO_THINS (25 unidades, el más alto)
        assertThat(result.getTopSku()).isEqualTo("OREO_THINS");

        // Top Branch debe ser Miraflores (15 + 25 = 40 unidades vs San Isidro 15 + 10 = 25)
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    /**
     * Test Bonus: Verifica el cálculo correcto con decimales complejos
     */
    @Test
    @DisplayName("Bonus: Debe calcular revenue correctamente con decimales complejos")
    void shouldCalculateRevenueCorrectlyWithComplexDecimals() {
        // Given - Precios con decimales complejos
        List<Sale> mockSales = List.of(
                createSale(1L, "OREO_CLASSIC", 7, new BigDecimal("1.99"), "Miraflores"),    // 13.93
                createSale(2L, "OREO_DOUBLE", 3, new BigDecimal("2.49"), "San Isidro"),     // 7.47
                createSale(3L, "OREO_THINS", 11, new BigDecimal("2.19"), "Miraflores")      // 24.09
        );

        // When
        SalesAggregatesResponse result = salesAggregationService.calculateAggregates(mockSales);

        // Then - Revenue: 13.93 + 7.47 + 24.09 = 45.49
        assertThat(result.getTotalRevenue())
                .isEqualByComparingTo(new BigDecimal("45.49"));
    }

    // ============== MÉTODOS AUXILIARES ==============

    /**
     * Crea una venta de prueba con fecha por defecto
     */
    private Sale createSale(Long id, String sku, int units, BigDecimal price, String branch) {
        return createSaleWithDate(id, sku, units, price, branch, LocalDateTime.now());
    }

    /**
     * Crea una venta de prueba con fecha específica
     */
    private Sale createSaleWithDate(Long id, String sku, int units, BigDecimal price, String branch, LocalDateTime soldAt) {
        Sale sale = new Sale();
        sale.setId(id);
        sale.setSku(sku);
        sale.setUnits(units);
        sale.setPrice(price);
        sale.setBranch(branch);
        sale.setSoldAt(soldAt);
        sale.setCreatedBy("test-user");
        sale.setCreatedAt(LocalDateTime.now());
        sale.setUpdatedAt(LocalDateTime.now());
        return sale;
    }
}
