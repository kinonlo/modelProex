package org.audit.modelproxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.audit.modelproxy.model.AuditRequest;
import org.audit.modelproxy.model.AuditResponse;
import org.audit.modelproxy.model.enums.AuditDecision;
import org.audit.modelproxy.model.enums.ContentType;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelAuditService {

    private final ContentAuditAiService aiService;
    private final ObjectMapper objectMapper;

    public AuditResponse audit(AuditRequest request) {
        log.info("Received audit request for contentId: {}", request.getContentId());
        long startTime = System.currentTimeMillis();
        
        String contentToAudit = request.getContent();
        if (request.getContentType() != ContentType.TEXT) {
             contentToAudit = "Content Type: " + request.getContentType() + ", Content: " + request.getContent();
        }

        try {
            String jsonResult = aiService.audit(contentToAudit);
            log.info("AI Audit Result: {}", jsonResult);
            
            String cleanJson = jsonResult.replace("```json", "").replace("```", "").trim();
            
            AuditResponse response = objectMapper.readValue(cleanJson, AuditResponse.class);
            response.setProcessingTime(System.currentTimeMillis() - startTime);
            return response;
        } catch (Exception e) {
            log.error("Error during AI audit", e);
            AuditResponse errorResponse = new AuditResponse();
            errorResponse.setDecision(AuditDecision.REVIEW);
            errorResponse.setReason("AI Service Error: " + e.getMessage());
            errorResponse.setConfidence(0.0);
            errorResponse.setProcessingTime(System.currentTimeMillis() - startTime);
            return errorResponse;
        }
    }
}
