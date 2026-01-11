package org.audit.modelproxy.controller;

import lombok.RequiredArgsConstructor;
import org.audit.modelproxy.model.AuditRequest;
import org.audit.modelproxy.model.AuditResponse;
import org.audit.modelproxy.service.ModelAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/model-proxy")
@RequiredArgsConstructor
public class ModelAuditController {

    private final ModelAuditService modelAuditService;

    @PostMapping("/execute")
    public ResponseEntity<AuditResponse> execute(@RequestBody AuditRequest request) {
        return ResponseEntity.ok(modelAuditService.audit(request));
    }
}
