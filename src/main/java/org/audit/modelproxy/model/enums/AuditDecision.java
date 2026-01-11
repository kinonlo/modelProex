package org.audit.modelproxy.model.enums;

public enum AuditDecision {
    PASS,       // 通过
    REJECT,     // 拒绝
    REVIEW,     // 需要人工复核
    UNCLEAR     // 无法判定
}
