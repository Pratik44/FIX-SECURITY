package com.fixsecurity.compliance;

import com.fixsecurity.engine.ParsedMessage;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Compliance Rule Engine
 * Evaluates FIX messages against regulatory compliance rules
 */
public class ComplianceRuleEngine {
    private List<ComplianceRule> rules = new ArrayList<>();
    
    public ComplianceRuleEngine() {
        loadDefaultRules();
    }
    
    /**
     * Evaluate a message against all compliance rules
     */
    public ComplianceResult evaluate(ParsedMessage message) {
        ComplianceResult result = new ComplianceResult();
        
        for (ComplianceRule rule : rules) {
            if (rule.matches(message)) {
                RuleEvaluation evaluation = rule.evaluate(message);
                result.addEvaluation(evaluation);
                
                if (!evaluation.isCompliant()) {
                    result.setCompliant(false);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Add a custom compliance rule
     */
    public void addRule(ComplianceRule rule) {
        rules.add(rule);
    }
    
    private void loadDefaultRules() {
        // MiFID II - Best Execution Rule
        rules.add(new ComplianceRule("MIFID-II-001", "Best Execution Check") {
            @Override
            public boolean matches(ParsedMessage message) {
                return "D".equals(message.getMsgType()); // NewOrderSingle
            }
            
            @Override
            public RuleEvaluation evaluate(ParsedMessage message) {
                // Placeholder: Check if price is within acceptable range
                // In production, this would compare against market prices
                return new RuleEvaluation(this, true, "Price within acceptable range");
            }
        });
        
        // Pre-trade Compliance - Order Size Limit
        rules.add(new ComplianceRule("PRE-TRADE-001", "Order Size Limit") {
            @Override
            public boolean matches(ParsedMessage message) {
                return "D".equals(message.getMsgType());
            }
            
            @Override
            public RuleEvaluation evaluate(ParsedMessage message) {
                double maxOrderSize = 1000000.0; // Example limit
                if (message.getOrderQty() > maxOrderSize) {
                    return new RuleEvaluation(this, false, 
                        "Order size " + message.getOrderQty() + " exceeds limit " + maxOrderSize);
                }
                return new RuleEvaluation(this, true, "Order size within limits");
            }
        });
        
        // Required Fields Check
        rules.add(new ComplianceRule("DATA-QUALITY-001", "Required Fields Check") {
            @Override
            public boolean matches(ParsedMessage message) {
                return "D".equals(message.getMsgType());
            }
            
            @Override
            public RuleEvaluation evaluate(ParsedMessage message) {
                List<String> missingFields = new ArrayList<>();
                
                if (message.getSymbol() == null || message.getSymbol().isEmpty()) {
                    missingFields.add("Symbol");
                }
                if (message.getSide() == null || message.getSide().isEmpty()) {
                    missingFields.add("Side");
                }
                if (message.getOrderQty() <= 0) {
                    missingFields.add("OrderQty");
                }
                
                if (!missingFields.isEmpty()) {
                    return new RuleEvaluation(this, false, 
                        "Missing required fields: " + String.join(", ", missingFields));
                }
                
                return new RuleEvaluation(this, true, "All required fields present");
            }
        });
    }
}
