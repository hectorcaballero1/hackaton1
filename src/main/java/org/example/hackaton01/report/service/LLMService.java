package org.example.hackaton01.report.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackaton01.sale.saleagregation.dto.SalesAggregatesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMService {

    private final ChatCompletionsClient chatClient;

    @Value("${github.models.model-id}")
    private String modelId;

    public String generateSummary(SalesAggregatesResponse aggregates) {
        try {
            log.info("Generando resumen para {} unidades, ${} revenue",
                    aggregates.getTotalUnits(), aggregates.getTotalRevenue());

            String userPrompt = String.format(
                    "Con estos datos de ventas semanales: " +
                            "totalUnits=%d, totalRevenue=%.2f, topSku=%s, topBranch=%s. " +
                            "Genera un resumen ejecutivo en español de máximo 120 palabras " +
                            "para enviar por email a gerentes. Sé conciso, profesional y destaca " +
                            "los insights más importantes.",
                    aggregates.getTotalUnits(),
                    aggregates.getTotalRevenue(),
                    aggregates.getTopSku(),
                    aggregates.getTopBranch()
            );

            List<ChatRequestMessage> messages = Arrays.asList(
                    new ChatRequestSystemMessage(
                            "Eres un analista de datos especializado en retail que escribe " +
                                    "resúmenes breves y claros para emails corporativos. " +
                                    "Siempre incluyes números concretos y insights accionables."
                    ),
                    new ChatRequestUserMessage(userPrompt)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                    .setModel(modelId)
                    .setMaxTokens(200)
                    .setTemperature(0.7);

            ChatCompletions completions = chatClient.complete(options);
            String summary = completions.getChoices().get(0).getMessage().getContent();

            log.info("Resumen generado exitosamente: {} caracteres", summary.length());
            return summary.trim();

        } catch (Exception e) {
            log.error("Error al generar resumen con LLM: {}", e.getMessage(), e);
            return buildFallbackSummary(aggregates);
        }
    }

    private String buildFallbackSummary(SalesAggregatesResponse aggregates) {
        return String.format(
                "Resumen de ventas Oreo: Se vendieron %d unidades con un revenue total de $%.2f. " +
                        "El SKU más vendido fue %s y la sucursal con mejor desempeño fue %s.",
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTopSku(),
                aggregates.getTopBranch()
        );
    }
}