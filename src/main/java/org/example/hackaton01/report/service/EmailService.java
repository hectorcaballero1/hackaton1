package org.example.hackaton01.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackaton01.exception.ServiceUnavailableException;
import org.example.hackaton01.sale.saleagregation.dto.SalesAggregatesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendReport(String toEmail, SalesAggregatesResponse aggregates,
                           String summary, LocalDate from, LocalDate to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(buildSubject(from, to));
            message.setText(buildEmailText(aggregates, summary, from, to));

            mailSender.send(message);
            log.info("Email enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", toEmail, e.getMessage());
            throw new ServiceUnavailableException(
                "El servicio de email no está disponible en este momento. " +
                "Por favor, intente más tarde. Error: " + e.getMessage()
            );
        }
    }

    private String buildSubject(LocalDate from, LocalDate to) {
        return String.format("📊 Reporte Semanal Oreo - %s a %s", from, to);
    }

    private String buildEmailText(SalesAggregatesResponse aggregates, String summary,
                                  LocalDate from, LocalDate to) {
        return String.format(
                "🍪 Reporte Semanal Oreo\n\n" +
                        "📅 Período: %s a %s\n\n" +
                        "📈 Métricas:\n" +
                        "   • Unidades vendidas: %d\n" +
                        "   • Revenue total: $%.2f\n" +
                        "   • SKU más vendido: %s\n" +
                        "   • Sucursal top: %s\n\n" +
                        "💡 Resumen ejecutivo:\n%s\n\n" +
                        "Saludos,\nEquipo Oreo Insights",
                from, to,
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTopSku(),
                aggregates.getTopBranch(),
                summary
        );
    }


    public void sendWelcomeEmail(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info(" Email de bienvenida enviado a: {}", toEmail);

        } catch (Exception e) {
            log.error(" Error enviando email de bienvenida a {}: {}", toEmail, e.getMessage());

        }
    }
}