package org.audit.modelproxy.model;

import lombok.Data;
import org.audit.modelproxy.model.enums.AuditDecision;
import java.util.Map;

@Data
public class AuditResponse {
    private AuditDecision decision;
    private Double confidence;
    private String reason;
    private Map<String, Object> analysis;
    private Long processingTime;
}
