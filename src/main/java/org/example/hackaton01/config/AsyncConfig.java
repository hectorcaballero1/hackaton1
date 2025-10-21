package org.example.hackaton01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Esta configuración habilita el procesamiento asíncrono con @Async
    // Necesario para el procesamiento de reportes en background
}
