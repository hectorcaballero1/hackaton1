package org.example.hackaton01.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySummaryResponse {
    private String requestId;
    private String status;
    private String message;
    private String estimatedTime;
    private LocalDateTime requestedAt;
}
