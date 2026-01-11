package org.audit.modelproxy.model;

import lombok.Data;
import org.audit.modelproxy.model.enums.ContentType;
import java.util.Map;

@Data
public class AuditRequest {
    private String contentId;
    private ContentType contentType;
    private String content;
    private String authorId;
    private Map<String, Object> metadata;
    private Integer priority;
}
