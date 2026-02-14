package com.fixsecurity.compliance;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of compliance evaluation
 */
public class ComplianceResult {
    private List<RuleEvaluation> evaluations = new ArrayList<>();
    private boolean compliant = true;
    
    public void addEvaluation(RuleEvaluation evaluation) {
        evaluations.add(evaluation);
        if (!evaluation.isCompliant()) {
            compliant = false;
        }
    }
    
    public List<RuleEvaluation> getEvaluations() {
        return evaluations;
    }
    
    public boolean isCompliant() {
        return compliant;
    }
    
    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }
}
