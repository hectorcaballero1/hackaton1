package org.example.hackaton01.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackaton01.auth.utils.SecurityContextUtil;
import org.example.hackaton01.report.dto.ReportRequest;
import org.example.hackaton01.report.dto.ReportResponse;
import org.example.hackaton01.report.events.ReportRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ApplicationEventPublisher eventPublisher;
    private final SecurityContextUtil securityContextUtil;


    public ReportResponse requestWeeklyReport(ReportRequest request) {
        log.info(" Solicitando reporte semanal para {}", request.getEmailTo());

        try {
            validateBranchPermission(request.getBranch());


            LocalDate from = processDate(request.getFrom(), true);
            LocalDate to = processDate(request.getTo(), false);

            if (request.getEmailTo() == null || request.getEmailTo().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo emailTo es obligatorio");
            }


            String requestId = generateRequestId();


            publishReportEvent(requestId, from, to, request);

            return buildAcceptedResponse(requestId);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(" Error solicitando reporte: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno procesando la solicitud");
        }
    }


    private void validateBranchPermission(String targetBranch) {
        String userRole = securityContextUtil.getCurrentUserRole();
        String userBranch = securityContextUtil.getCurrentUserBranch();

        // Usuario BRANCH solo puede acceder a su sucursal
        if ("BRANCH".equals(userRole) && !targetBranch.equals(userBranch)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para generar reportes de esta sucursal");
        }
    }



    private LocalDate processDate(String dateString, boolean isFromDate) {
        if (dateString != null && !dateString.trim().isEmpty()) {
            return LocalDate.parse(dateString);
        }
        // Si no viene fecha, calcular automáticamente
        return isFromDate ? LocalDate.now().minusDays(7) : LocalDate.now();
    }

    private String generateRequestId() {
        return "req_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    private void publishReportEvent(String requestId, LocalDate from, LocalDate to, ReportRequest request) {
        ReportRequestedEvent event = new ReportRequestedEvent(
                this,
                requestId,
                from,
                to,
                request.getBranch(),
                request.getEmailTo(),
                securityContextUtil.getCurrentUsername()
        );

        eventPublisher.publishEvent(event);
        log.info(" Evento publicado para requestId: {}", requestId);
    }

    private ReportResponse buildAcceptedResponse(String requestId) {
        return ReportResponse.builder()
                .requestId(requestId)
                .status("PROCESSING")
                .message("Su solicitud de reporte está siendo procesada. Recibirá el resumen por email en unos momentos.")
                .estimatedTime("30-60 segundos")
                .requestedAt(LocalDateTime.now())
                .build();
    }
}