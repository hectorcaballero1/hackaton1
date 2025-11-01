package org.example.hackaton01.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hackaton01.report.dto.ReportRequest;
import org.example.hackaton01.report.dto.ReportResponse;
import org.example.hackaton01.report.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales/summary")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * POST /sales/summary/weekly - ÚNICO endpoint del módulo
     * Generación asíncrona de reporte semanal
     */
    @PostMapping("/weekly")
    public ResponseEntity<ReportResponse> generateWeeklySummary(@Valid @RequestBody ReportRequest request) {
        ReportResponse response = reportService.requestWeeklyReport(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}