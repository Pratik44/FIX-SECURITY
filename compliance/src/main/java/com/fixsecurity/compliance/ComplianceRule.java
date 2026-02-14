package com.fixsecurity.compliance;

import com.fixsecurity.engine.ParsedMessage;

/**
 * Abstract base class for compliance rules
 */
public abstract class ComplianceRule {
    private String ruleId;
    private String name;
    private String description;
    
    public ComplianceRule(String ruleId, String name) {
        this.ruleId = ruleId;
        this.name = name;
    }
    
    /**
     * Check if this rule applies to the given message
     */
    public abstract boolean matches(ParsedMessage message);
    
    /**
     * Evaluate the message against this rule
     */
    public abstract RuleEvaluation evaluate(ParsedMessage message);
    
    public String getRuleId() { return ruleId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
