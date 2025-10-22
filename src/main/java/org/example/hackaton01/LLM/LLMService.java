package org.example.hackaton01.LLM;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMService {

    private final ChatCompletionsClient chatClient;

    @Value("${github.models.model-id}")
    private String modelId;

    /**
     * Genera un resumen de ventas usando el LLM
     * @param totalUnits Total de unidades vendidas
     * @param totalRevenue Total de ingresos
     * @param topSku SKU más vendido
     * @param topBranch Sucursal con más ventas
     * @return Resumen en lenguaje natural (máx 120 palabras)
     */

    public String generateSalesSummary(int totalUnits, double totalRevenue,
                                       String topSku, String topBranch) {
        try {
            log.info("Generando resumen con LLM para {} unidades, ${} revenue",
                    totalUnits, totalRevenue);

            String userPrompt = String.format(
                    "Con estos datos de ventas semanales: " +
                            "totalUnits=%d, totalRevenue=%.2f, topSku=%s, topBranch=%s. " +
                            "Genera un resumen ejecutivo en español de máximo 120 palabras " +
                            "para enviar por email a gerentes. Sé conciso, profesional y destaca " +
                            "los insights más importantes.",
                    totalUnits, totalRevenue, topSku, topBranch
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
            throw new LLMServiceException("No se pudo generar el resumen. " +
                    "El servicio de IA no está disponible.", e);
        }
    }

    /**
     * Excepción personalizada para errores del LLM
     */
    public static class LLMServiceException extends RuntimeException {
        public LLMServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}