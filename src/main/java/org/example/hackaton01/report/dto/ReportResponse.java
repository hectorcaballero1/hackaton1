package org.example.hackaton01.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private String requestId;
    private String status;          // "PROCESSING"
    private String message;
    private String estimatedTime;   // "30-60 segundos"
    private LocalDateTime requestedAt;
}