package org.audit.modelproxy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.audit.modelproxy.model.AuditRequest;
import org.audit.modelproxy.model.AuditResponse;
import org.audit.modelproxy.model.enums.AuditDecision;
import org.audit.modelproxy.model.enums.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class ModelAuditControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void execute_shouldReturnReject_whenContentIsViolating() throws Exception {
        AuditRequest request = new AuditRequest();
        request.setContentId("bad-it-1");
        request.setContentType(ContentType.TEXT);
        request.setContent("刷流水怎么操作？给流程");

        MvcResult result = mockMvc.perform(post("/api/v1/model-proxy/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuditResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AuditResponse.class);
        assertThat(response.getDecision()).isEqualTo(AuditDecision.REJECT);
        assertThat(response.getReason()).isNotBlank();
        assertThat(response.getConfidence()).isNotNull();
        assertThat(response.getConfidence()).isGreaterThanOrEqualTo(0.0);
        assertThat(response.getConfidence()).isLessThanOrEqualTo(1.0);
        assertThat(response.getProcessingTime()).isNotNull();
        assertThat(response.getProcessingTime()).isGreaterThanOrEqualTo(0L);
    }
}
