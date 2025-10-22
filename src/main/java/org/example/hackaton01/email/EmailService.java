package org.example.hackaton01.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envía el resumen semanal por email
     */
    public void sendWeeklySummary(String toEmail, LocalDate fromDate, LocalDate toDate,
                                   String branch, String summaryText,
                                   Integer totalUnits, Double totalRevenue,
                                   String topSku, String topBranch) {
        try {
            log.info("Enviando resumen semanal a: {}", toEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(buildSubject(fromDate, toDate, branch));
            message.setText(buildEmailBody(summaryText, totalUnits, totalRevenue, topSku, topBranch, fromDate, toDate));

            mailSender.send(message);

            log.info("Email enviado exitosamente a: {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", toEmail, e.getMessage(), e);
            throw new EmailServiceException("No se pudo enviar el email. " +
                    "El servicio de correo no está disponible.", e);
        }
    }

    private String buildSubject(LocalDate from, LocalDate to, String branch) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateRange = from.format(formatter) + " a " + to.format(formatter);

        if (branch != null) {
            return String.format("Reporte Semanal Oreo - %s - %s", branch, dateRange);
        }
        return String.format("Reporte Semanal Oreo - %s", dateRange);
    }

    private String buildEmailBody(String summaryText, Integer totalUnits, Double totalRevenue,
                                   String topSku, String topBranch, LocalDate from, LocalDate to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder body = new StringBuilder();
        body.append("🍪 REPORTE SEMANAL DE VENTAS OREO\n");
        body.append("═══════════════════════════════════\n\n");
        body.append("📅 Período: ").append(from.format(formatter))
            .append(" - ").append(to.format(formatter)).append("\n\n");

        body.append("📊 RESUMEN EJECUTIVO:\n");
        body.append("───────────────────────────────────\n");
        body.append(summaryText).append("\n\n");

        body.append("📈 MÉTRICAS CLAVE:\n");
        body.append("───────────────────────────────────\n");
        body.append(String.format("• Total Unidades: %,d\n", totalUnits));
        body.append(String.format("• Ingresos Totales: $%,.2f\n", totalRevenue));
        body.append(String.format("• SKU Más Vendido: %s\n", topSku != null ? topSku : "N/A"));
        body.append(String.format("• Sucursal Líder: %s\n", topBranch != null ? topBranch : "N/A"));

        body.append("\n───────────────────────────────────\n");
        body.append("🤖 Generado con Claude Code\n");
        body.append("Oreo Insight Factory © 2025\n");

        return body.toString();
    }

    /**
     * Excepción personalizada para errores del servicio de email
     */
    public static class EmailServiceException extends RuntimeException {
        public EmailServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
