package com.fixsecurity.compliance;

/**
 * Result of evaluating a single compliance rule
 */
public class RuleEvaluation {
    private ComplianceRule rule;
    private boolean compliant;
    private String message;
    private long timestamp;
    
    public RuleEvaluation(ComplianceRule rule, boolean compliant, String message) {
        this.rule = rule;
        this.compliant = compliant;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ComplianceRule getRule() { return rule; }
    public boolean isCompliant() { return compliant; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
