package com.fixsecurity.security;

import com.fixsecurity.engine.ParsedMessage;
import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Anomaly Detection Engine for FIX messages
 * Detects unusual patterns and potential security threats
 */
public class AnomalyDetector {
    private Map<String, SessionBaseline> sessionBaselines = new HashMap<>();
    private Map<String, List<LocalDateTime>> messageTimestamps = new HashMap<>();
    private double anomalyThreshold = 2.0; // Standard deviations
    
    /**
     * Analyze a FIX message for anomalies
     */
    public AnomalyResult detectAnomalies(ParsedMessage message) {
        AnomalyResult result = new AnomalyResult();
        String sessionId = message.getSenderCompID() + "-" + message.getTargetCompID();
        
        // Check message volume anomaly
        if (isVolumeAnomaly(sessionId)) {
            result.addAnomaly("HIGH_MESSAGE_VOLUME", 
                "Unusual high message volume detected for session: " + sessionId);
        }
        
        // Check message type anomaly
        if (isMessageTypeAnomaly(sessionId, message.getMsgType())) {
            result.addAnomaly("UNUSUAL_MESSAGE_TYPE", 
                "Unusual message type detected: " + message.getMsgType());
        }
        
        // Check sequence number anomaly
        if (isSequenceAnomaly(sessionId, message.getMsgSeqNum())) {
            result.addAnomaly("SEQUENCE_NUMBER_ANOMALY", 
                "Sequence number gap detected: " + message.getMsgSeqNum());
        }
        
        // Check price anomaly (for orders)
        if ("D".equals(message.getMsgType()) && message.getPrice() > 0) {
            if (isPriceAnomaly(message.getSymbol(), message.getPrice())) {
                result.addAnomaly("PRICE_ANOMALY", 
                    "Unusual price detected: " + message.getPrice() + " for " + message.getSymbol());
            }
        }
        
        // Update baseline
        updateBaseline(sessionId, message);
        
        return result;
    }
    
    private boolean isVolumeAnomaly(String sessionId) {
        List<LocalDateTime> timestamps = messageTimestamps.getOrDefault(sessionId, new ArrayList<>());
        LocalDateTime now = LocalDateTime.now();
        
        // Count messages in last minute
        long messagesLastMinute = timestamps.stream()
            .filter(t -> ChronoUnit.SECONDS.between(t, now) < 60)
            .count();
        
        SessionBaseline baseline = sessionBaselines.get(sessionId);
        if (baseline == null) {
            return false;
        }
        
        double avgMessagesPerMinute = baseline.getAvgMessagesPerMinute();
        return messagesLastMinute > (avgMessagesPerMinute * anomalyThreshold);
    }
    
    private boolean isMessageTypeAnomaly(String sessionId, String msgType) {
        SessionBaseline baseline = sessionBaselines.get(sessionId);
        if (baseline == null) {
            return false;
        }
        
        Map<String, Integer> typeDistribution = baseline.getMessageTypeDistribution();
        int totalMessages = typeDistribution.values().stream().mapToInt(Integer::intValue).sum();
        
        if (totalMessages == 0) {
            return false;
        }
        
        double expectedFrequency = typeDistribution.getOrDefault(msgType, 0) / (double) totalMessages;
        return expectedFrequency < 0.01; // Less than 1% expected frequency
    }
    
    private boolean isSequenceAnomaly(String sessionId, int seqNum) {
        SessionBaseline baseline = sessionBaselines.get(sessionId);
        if (baseline == null) {
            return false;
        }
        
        int lastSeqNum = baseline.getLastSequenceNumber();
        if (lastSeqNum > 0 && seqNum > lastSeqNum + 10) {
            return true; // Gap of more than 10 sequence numbers
        }
        
        return false;
    }
    
    private boolean isPriceAnomaly(String symbol, double price) {
        // Simple price anomaly detection based on deviation from recent average
        // In production, this would use more sophisticated statistical methods
        return false; // Placeholder
    }
    
    private void updateBaseline(String sessionId, ParsedMessage message) {
        SessionBaseline baseline = sessionBaselines.computeIfAbsent(
            sessionId, 
            k -> new SessionBaseline()
        );
        
        baseline.incrementMessageCount();
        baseline.updateLastSequenceNumber(message.getMsgSeqNum());
        baseline.updateMessageTypeDistribution(message.getMsgType());
        
        // Update timestamps
        List<LocalDateTime> timestamps = messageTimestamps.computeIfAbsent(
            sessionId,
            k -> new ArrayList<>()
        );
        timestamps.add(LocalDateTime.now());
        
        // Keep only last hour of timestamps
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        timestamps.removeIf(t -> t.isBefore(oneHourAgo));
    }
}
