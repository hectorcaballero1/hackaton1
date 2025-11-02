package org.example.hackaton01.report.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackaton01.exception.ServiceUnavailableException;
import org.example.hackaton01.report.service.EmailService;
import org.example.hackaton01.report.service.LLMService;
import org.example.hackaton01.sale.domain.Sale;
import org.example.hackaton01.sale.domain.SaleService;
import org.example.hackaton01.sale.saleagregation.dto.SalesAggregatesResponse;
import org.example.hackaton01.sale.saleagregation.service.SalesAggregationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final SaleService saleService;
    private final SalesAggregationService salesAggregationService; //  Usa el servicio existente
    private final LLMService llmService;
    private final EmailService emailService;

    @Async
    @EventListener
    public void processReportAsync(ReportRequestedEvent event) {
        try {
            log.info(" Procesando reporte asíncrono: {}", event.getRequestId());

            List<Sale> sales = saleService.findSalesForReport(
                    event.getFrom().atStartOfDay(),
                    event.getTo().atTime(23, 59, 59),
                    event.getBranch()
            );

            SalesAggregatesResponse aggregates = salesAggregationService.calculateAggregates(sales);


            String summary = llmService.generateSummary(aggregates);

            // envio de email
            emailService.sendReport(
                    event.getEmailTo(),
                    aggregates,
                    summary,
                    event.getFrom(),
                    event.getTo()
            );

            log.info("Reporte {} enviado a {}", event.getRequestId(), event.getEmailTo());

        } catch (ServiceUnavailableException e) {
            log.error("Servicio no disponible para reporte {}: {}", event.getRequestId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en reporte {}: {}", event.getRequestId(), e.getMessage(), e);
        }
    }
}