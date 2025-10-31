package org.example.hackaton01.report.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class ReportRequestedEvent extends ApplicationEvent { //  Extiende ApplicationEvent

    private final String requestId;
    private final LocalDate from;
    private final LocalDate to;
    private final String branch;
    private final String emailTo;
    private final String requestedBy;

    public ReportRequestedEvent(Object source, String requestId, LocalDate from,
                                LocalDate to, String branch, String emailTo, String requestedBy) {
        super(source); //  Llama al constructor padre
        this.requestId = requestId;
        this.from = from;
        this.to = to;
        this.branch = branch;
        this.emailTo = emailTo;
        this.requestedBy = requestedBy;
    }
}