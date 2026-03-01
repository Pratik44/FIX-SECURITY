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
        anomalies.add(new Anomaly(type, description, null, null));
        hasAnomalies = true;
    }

    /**
     * Add an anomaly with optional order identifiers for analytics reporting.
     */
    public void addAnomaly(String type, String description, String orderId, String clOrdID) {
        anomalies.add(new Anomaly(type, description, orderId, clOrdID));
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
        private String orderId;
        private String clOrdID;

        public Anomaly(String type, String description) {
            this(type, description, null, null);
        }

        public Anomaly(String type, String description, String orderId, String clOrdID) {
            this.type = type;
            this.description = description;
            this.timestamp = System.currentTimeMillis();
            this.orderId = orderId;
            this.clOrdID = clOrdID;
        }

        public String getType() { return type; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
        public String getOrderId() { return orderId; }
        public String getClOrdID() { return clOrdID; }
    }
}
