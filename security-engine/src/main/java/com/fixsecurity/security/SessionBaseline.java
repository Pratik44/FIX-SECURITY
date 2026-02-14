package com.fixsecurity.security;

import java.util.HashMap;
import java.util.Map;

/**
 * Baseline behavior for a FIX session
 */
public class SessionBaseline {
    private long messageCount = 0;
    private int lastSequenceNumber = 0;
    private Map<String, Integer> messageTypeDistribution = new HashMap<>();
    private long startTime = System.currentTimeMillis();
    
    public void incrementMessageCount() {
        messageCount++;
    }
    
    public void updateLastSequenceNumber(int seqNum) {
        this.lastSequenceNumber = seqNum;
    }
    
    public void updateMessageTypeDistribution(String msgType) {
        messageTypeDistribution.put(msgType, 
            messageTypeDistribution.getOrDefault(msgType, 0) + 1);
    }
    
    public double getAvgMessagesPerMinute() {
        long elapsedMinutes = (System.currentTimeMillis() - startTime) / 60000;
        if (elapsedMinutes == 0) {
            return messageCount;
        }
        return messageCount / (double) elapsedMinutes;
    }
    
    public long getMessageCount() { return messageCount; }
    public int getLastSequenceNumber() { return lastSequenceNumber; }
    public Map<String, Integer> getMessageTypeDistribution() { return messageTypeDistribution; }
}
