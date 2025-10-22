package org.example.hackaton01.sale.infrastructure;

import org.example.hackaton01.sale.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {

    // Buscar ventas por sucursal
    Page<Sale> findByBranch(String branch, Pageable pageable);

    // Buscar ventas por rango de fechas
    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN :from AND :to")
    Page<Sale> findBySoldAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    // Buscar ventas por sucursal y rango de fechas
    @Query("SELECT s FROM Sale s WHERE s.branch = :branch AND s.soldAt BETWEEN :from AND :to")
    Page<Sale> findByBranchAndSoldAtBetween(@Param("branch") String branch, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    // Buscar ventas por rango de fechas (sin paginación, para agregados)
    @Query("SELECT s FROM Sale s WHERE s.soldAt BETWEEN :from AND :to")
    List<Sale> findBySoldAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Buscar ventas por sucursal y rango de fechas (sin paginación, para agregados)
    @Query("SELECT s FROM Sale s WHERE s.branch = :branch AND s.soldAt BETWEEN :from AND :to")
    List<Sale> findByBranchAndSoldAtBetween(@Param("branch") String branch, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
