package org.example.hackaton01.report.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private String from;

    private String to;

    private String branch;
    @NotBlank(message = "emailTo es obligatorio")
    @Email(message = "emailTo debe ser un email válido")
    private String emailTo;
}