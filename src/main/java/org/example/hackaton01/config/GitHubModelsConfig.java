package org.example.hackaton01.config;


import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GitHubModelsConfig {
    // Líneas 1-3: Configuración (ahora con @Value)
    @Value("${github.models.token}")
    private String githubToken;

    @Value("${github.models.endpoint:https://models.github.ai/inference}")
    private String endpoint;

    @Value("${github.models.model-id:openai/gpt-5-mini}")
    private String modelId;

    // Líneas 4-7: Crear cliente (ahora como Bean)
    @Bean
    public ChatCompletionsClient chatCompletionsClient() {
        return new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(githubToken))
                .endpoint(endpoint)
                .buildClient();
    }
}