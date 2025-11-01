package org.example.hackaton01.sale.infrastructure;

import org.example.hackaton01.sale.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    // Agrega este método en tu repository
    List<Sale> findBySoldAtBetweenAndBranch(LocalDateTime startDate, LocalDateTime endDate, String branch);
    // para los reportes asincronicos
    List<Sale> findByBranch(String branch);
    // para el reposrte asincrono (filtrado por fecha para CENTRAL)
    List<Sale> findBySoldAtBetween(LocalDateTime start, LocalDateTime end);
    // para el reposte asincronico (filtrado por fecha y por branchs)
    List<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime from, LocalDateTime to);
    // para los endopints
    //  PAGINACIÓN: Ventas por sucursal (para usuarios BRANCH)
    Page<Sale> findByBranch(String branch, Pageable pageable);

    // PAGINACIÓN: Ventas por sucursal Y rango de fechas (para reportes y filtros)
    Page<Sale> findByBranchAndSoldAtBetween(String branch, LocalDateTime start, LocalDateTime end, Pageable pageable);

    //  PAGINACIÓN: Ventas por rango de fechas (para usuarios CENTRAL)
    Page<Sale> findBySoldAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
