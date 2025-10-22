package org.example.hackaton01.sale.domain;

import lombok.RequiredArgsConstructor;
import org.example.hackaton01.auth.utils.SecurityContextUtil;
import org.example.hackaton01.sale.Sale;
import org.example.hackaton01.sale.dto.SaleRequest;
import org.example.hackaton01.sale.dto.SaleResponse;
import org.example.hackaton01.sale.dto.SaleUpdateRequest;
import org.example.hackaton01.sale.infrastructure.SaleRepository;
import org.example.hackaton01.user.domain.Role;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SecurityContextUtil securityContextUtil;
    private final ModelMapper modelMapper;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        // Validar que usuarios BRANCH solo puedan crear ventas de su sucursal
        if (securityContextUtil.isBranchRole()) {
            String userBranch = securityContextUtil.getCurrentBranch();
            if (!userBranch.equals(request.getBranch())) {
                throw new RuntimeException("Access denied: You can only create sales for your own branch");
            }
        }

        // Crear la venta
        Sale sale = new Sale();
        sale.setSku(request.getSku());
        sale.setUnits(request.getUnits());
        sale.setPrice(request.getPrice());
        sale.setBranch(request.getBranch());
        sale.setSoldAt(request.getSoldAt());
        sale.setCreatedBy(securityContextUtil.getCurrentUsername());

        Sale savedSale = saleRepository.save(sale);

        return modelMapper.map(savedSale, SaleResponse.class);
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(String id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Validar que usuarios BRANCH solo puedan ver ventas de su sucursal
        if (securityContextUtil.isBranchRole()) {
            String userBranch = securityContextUtil.getCurrentBranch();
            if (!userBranch.equals(sale.getBranch())) {
                throw new RuntimeException("Access denied: You can only access sales from your own branch");
            }
        }

        return modelMapper.map(sale, SaleResponse.class);
    }

    @Transactional(readOnly = true)
    public Page<SaleResponse> getAllSales(LocalDateTime from, LocalDateTime to, String branch, Pageable pageable) {
        Page<Sale> salesPage;

        // Si es usuario BRANCH, forzar filtro por su sucursal
        if (securityContextUtil.isBranchRole()) {
            String userBranch = securityContextUtil.getCurrentBranch();

            if (from != null && to != null) {
                salesPage = saleRepository.findByBranchAndSoldAtBetween(userBranch, from, to, pageable);
            } else {
                salesPage = saleRepository.findByBranch(userBranch, pageable);
            }
        } else {
            // Usuario CENTRAL puede ver todas o filtrar por branch
            if (branch != null && from != null && to != null) {
                salesPage = saleRepository.findByBranchAndSoldAtBetween(branch, from, to, pageable);
            } else if (from != null && to != null) {
                salesPage = saleRepository.findBySoldAtBetween(from, to, pageable);
            } else if (branch != null) {
                salesPage = saleRepository.findByBranch(branch, pageable);
            } else {
                salesPage = saleRepository.findAll(pageable);
            }
        }

        return salesPage.map(sale -> modelMapper.map(sale, SaleResponse.class));
    }

    @Transactional
    public SaleResponse updateSale(String id, SaleUpdateRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Validar que usuarios BRANCH solo puedan actualizar ventas de su sucursal
        if (securityContextUtil.isBranchRole()) {
            String userBranch = securityContextUtil.getCurrentBranch();
            if (!userBranch.equals(sale.getBranch())) {
                throw new RuntimeException("Access denied: You can only update sales from your own branch");
            }
            // Validar que no intenten cambiar la sucursal
            if (!userBranch.equals(request.getBranch())) {
                throw new RuntimeException("Access denied: You cannot change the branch");
            }
        }

        // Actualizar los campos
        sale.setSku(request.getSku());
        sale.setUnits(request.getUnits());
        sale.setPrice(request.getPrice());
        sale.setBranch(request.getBranch());
        sale.setSoldAt(request.getSoldAt());

        Sale updatedSale = saleRepository.save(sale);

        return modelMapper.map(updatedSale, SaleResponse.class);
    }

    @Transactional
    public void deleteSale(String id) {
        // Solo usuarios CENTRAL pueden eliminar
        if (!securityContextUtil.isCentralRole()) {
            throw new RuntimeException("Access denied: Only CENTRAL users can delete sales");
        }

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        saleRepository.delete(sale);
    }
}
