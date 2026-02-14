package com.fixsecurity.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of anomaly detection analysis
 */
public class AnomalyResult {
    private List<Anomaly> anomalies = new ArrayList<>();
    private boolean hasAnomalies = false;
    
    public void addAnomaly(String type, String description) {
        anomalies.add(new Anomaly(type, description));
        hasAnomalies = true;
    }
    
    public List<Anomaly> getAnomalies() {
        return anomalies;
    }
    
    public boolean hasAnomalies() {
        return hasAnomalies;
    }
    
    public static class Anomaly {
        private String type;
        private String description;
        private long timestamp;
        
        public Anomaly(String type, String description) {
            this.type = type;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getType() { return type; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
    }
}
