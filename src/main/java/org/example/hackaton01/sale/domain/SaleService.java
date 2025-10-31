package org.example.hackaton01.sale.domain;

import lombok.RequiredArgsConstructor;
import org.example.hackaton01.auth.utils.SecurityContextUtil;
import org.example.hackaton01.sale.dto.SaleRequest;
import org.example.hackaton01.sale.dto.SaleResponse;
import org.example.hackaton01.sale.dto.SaleUpdateRequest;
import org.example.hackaton01.sale.infrastructure.SaleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SecurityContextUtil securityContextUtil;
    private final ModelMapper modelMapper;
    public List<Sale> findSalesForReport(LocalDateTime startDate, LocalDateTime endDate, String branch) {
        // Implementación según tu JPA Repository
        return saleRepository.findBySoldAtBetweenAndBranch(startDate, endDate, branch);
    }

    /**   POST /sales - Crear venta * CENTRAL: cualquier branch, BRANCH: solo su branch
     */
    @Transactional
    public SaleResponse createSale(SaleRequest request) {

        validateBranchPermission(request.getBranch());

        Sale sale = modelMapper.map(request, Sale.class);
        sale.setCreatedBy(securityContextUtil.getCurrentUsername());
        Sale savedSale = saleRepository.save(sale);

        return convertToResponse(savedSale);
    }

    /**   GET /sales/{id} - Obtener venta por ID * CENTRAL: cualquier venta, BRANCH: solo de su branch
     */
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        validateBranchPermission(sale.getBranch());
        return convertToResponse(sale);
    }

    /**   GET /sales - Listar ventas CON PAGINACIÓN * Query params: from, to, branch, page, size
     * CENTRAL: todas las ventas, BRANCH: solo su branch
     */
    @Transactional(readOnly = true)
    public Page<SaleResponse> getAllSales(LocalDateTime from, LocalDateTime to, String branch, Pageable pageable) {
        Page<Sale> salesPage;

        if (securityContextUtil.isBranchRole()) {

            String userBranch = securityContextUtil.getCurrentBranch();
            salesPage = (from != null && to != null)
                    ? saleRepository.findByBranchAndSoldAtBetween(userBranch, from, to, pageable)
                    : saleRepository.findByBranch(userBranch, pageable);
        } else {

            salesPage = getSalesWithFilters(branch, from, to, pageable);
        }

        return salesPage.map(this::convertToResponse);
    }

    /**   PARA REPORTES ASÍNCRONOS - SIN PAGINACIÓN * Usado por: POST /sales/summary/weekly
     */
    @Transactional(readOnly = true)
    public List<Sale> getSalesForReport(LocalDateTime from, LocalDateTime to, String branch) {
        if (securityContextUtil.isBranchRole()) {

            String userBranch = securityContextUtil.getCurrentBranch();
            return (from != null && to != null)
                    ? saleRepository.findByBranchAndSoldAtBetween(userBranch, from, to)
                    : saleRepository.findByBranch(userBranch);
        } else {
            return getSalesWithFilters(branch, from, to);
        }
    }

    /**   PUT /sales/{id} - Actualizar venta * CENTRAL: cualquier venta, BRANCH: solo de su branch
     */
    @Transactional
    public SaleResponse updateSale(Long id, SaleUpdateRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));


        validateBranchPermission(sale.getBranch());


        if (securityContextUtil.isBranchRole() && !sale.getBranch().equals(request.getBranch())) {
            throw new RuntimeException("No puedes cambiar la sucursal");
        }

        modelMapper.map(request, sale);
        Sale updatedSale = saleRepository.save(sale);

        return convertToResponse(updatedSale);
    }

    /**   DELETE /sales/{id} - Eliminar venta * SOLO usuarios CENTRAL */
    @Transactional
    public void deleteSale(Long id) {
        if (!securityContextUtil.isCentralRole()) {
            throw new RuntimeException("Solo usuarios CENTRAL pueden eliminar ventas");
        }

        if (!saleRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }

        saleRepository.deleteById(id);
    }

    // metodos para la validacion de permisos y y la ocnvercion de

    /**   Validar permisos de branch (1 método reutilizable) */
    private void validateBranchPermission(String targetBranch) {
        if (securityContextUtil.isBranchRole() &&
                !targetBranch.equals(securityContextUtil.getCurrentBranch())) {
            throw new RuntimeException("No tienes permisos para esta sucursal: " + targetBranch);
        }
    }

    /**   Conversión Entity -> Response (1 método reutilizable) */
    private SaleResponse convertToResponse(Sale sale) {
        SaleResponse response = modelMapper.map(sale, SaleResponse.class);
//        response.setTotal(sale.getTotal());
        return response;
    }

    /*obtrener ventar con paginacion*/
    private Page<Sale> getSalesWithFilters(String branch, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if (branch != null && from != null && to != null) {
            return saleRepository.findByBranchAndSoldAtBetween(branch, from, to, pageable);
        } else if (from != null && to != null) {
            return saleRepository.findBySoldAtBetween(from, to, pageable);
        } else if (branch != null) {
            return saleRepository.findByBranch(branch, pageable);
        } else {
            return saleRepository.findAll(pageable);
        }
    }
    //obtrener ventar sin paginacion
    private List<Sale> getSalesWithFilters(String branch, LocalDateTime from, LocalDateTime to) {
        if (branch != null && from != null && to != null) {
            return saleRepository.findByBranchAndSoldAtBetween(branch, from, to);
        } else if (from != null && to != null) {
            return saleRepository.findBySoldAtBetween(from, to);
        } else if (branch != null) {
            return saleRepository.findByBranch(branch);
        } else {
            return saleRepository.findAll();
        }
    }
}