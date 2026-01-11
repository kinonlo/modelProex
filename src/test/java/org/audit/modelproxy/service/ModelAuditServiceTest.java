package org.audit.modelproxy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.audit.modelproxy.model.AuditRequest;
import org.audit.modelproxy.model.AuditResponse;
import org.audit.modelproxy.model.enums.AuditDecision;
import org.audit.modelproxy.model.enums.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ModelAuditServiceTest {

    @Mock
    private ContentAuditAiService aiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void audit_shouldParseJsonSuccessfully_whenAiReturnsPlainJson() throws Exception {
        ModelAuditService service = new ModelAuditService(aiService, objectMapper);

        AuditRequest request = new AuditRequest();
        request.setContentId("c1");
        request.setContentType(ContentType.TEXT);
        request.setContent("hello");

        when(aiService.audit("hello"))
                .thenReturn("{\"decision\":\"PASS\",\"reason\":\"ok\",\"confidence\":0.9}");

        AuditResponse response = service.audit(request);

        assertThat(response.getDecision()).isEqualTo(AuditDecision.PASS);
        assertThat(response.getReason()).isEqualTo("ok");
        assertThat(response.getConfidence()).isEqualTo(0.9);
        assertThat(response.getProcessingTime()).isNotNull();
        assertThat(response.getProcessingTime()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    public void audit_shouldStripCodeFence_whenAiReturnsJsonInMarkdownFence() {
        ModelAuditService service = new ModelAuditService(aiService, objectMapper);

        AuditRequest request = new AuditRequest();
        request.setContentId("c2");
        request.setContentType(ContentType.TEXT);
        request.setContent("hello");

        when(aiService.audit("hello"))
                .thenReturn("```json\n{\"decision\":\"REJECT\",\"reason\":\"bad\",\"confidence\":0.7}\n```");

        AuditResponse response = service.audit(request);

        assertThat(response.getDecision()).isEqualTo(AuditDecision.REJECT);
        assertThat(response.getReason()).isEqualTo("bad");
        assertThat(response.getConfidence()).isEqualTo(0.7);
    }

    @Test
    public void audit_shouldIncludeContentTypePrefix_whenContentTypeIsNotText() {
        ModelAuditService service = new ModelAuditService(aiService, objectMapper);

        AuditRequest request = new AuditRequest();
        request.setContentId("c3");
        request.setContentType(ContentType.IMAGE);
        request.setContent("http://example.com/a.png");

        when(aiService.audit(anyString()))
                .thenReturn("{\"decision\":\"REVIEW\",\"reason\":\"need check\",\"confidence\":0.5}");

        AuditResponse response = service.audit(request);

        verify(aiService).audit("Content Type: IMAGE, Content: http://example.com/a.png");
        assertThat(response.getDecision()).isEqualTo(AuditDecision.REVIEW);
        assertThat(response.getReason()).isEqualTo("need check");
        assertThat(response.getConfidence()).isEqualTo(0.5);
    }

    @Test
    public void audit_shouldFallbackToReview_whenAiThrowsException() {
        ModelAuditService service = new ModelAuditService(aiService, objectMapper);

        AuditRequest request = new AuditRequest();
        request.setContentId("c4");
        request.setContentType(ContentType.TEXT);
        request.setContent("hello");

        when(aiService.audit("hello")).thenThrow(new RuntimeException("boom"));

        AuditResponse response = service.audit(request);

        assertThat(response.getDecision()).isEqualTo(AuditDecision.REVIEW);
        assertThat(response.getReason()).contains("AI Service Error");
        assertThat(response.getReason()).contains("boom");
        assertThat(response.getConfidence()).isEqualTo(0.0);
        assertThat(response.getProcessingTime()).isNotNull();
        assertThat(response.getProcessingTime()).isGreaterThanOrEqualTo(0L);
    }
}
