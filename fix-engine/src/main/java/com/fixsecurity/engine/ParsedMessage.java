package com.fixsecurity.engine;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

/**
 * Represents a parsed FIX message with extracted fields
 */
public class ParsedMessage {
    private String msgType;
    private String senderCompID;
    private String targetCompID;
    private int msgSeqNum;
    private String sendingTime;
    private LocalDateTime parsedTime;
    
    // NewOrderSingle fields
    private String symbol;
    private String side;
    private double orderQty;
    private double price;
    private String ordType;
    private String timeInForce;
    private String clOrdID;
    
    // ExecutionReport fields
    private String orderID;
    private String execID;
    private String execType;
    private String ordStatus;
    private double lastQty;
    private double lastPx;
    private double cumQty;
    private double avgPx;
    
    // Logon fields
    private int encryptMethod;
    private int heartBtInt;
    private String username;
    
    // Logout fields
    private String text;
    
    // All fields as key-value pairs
    private Map<String, String> allFields = new HashMap<>();
    
    // Getters and Setters
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    
    public String getSenderCompID() { return senderCompID; }
    public void setSenderCompID(String senderCompID) { this.senderCompID = senderCompID; }
    
    public String getTargetCompID() { return targetCompID; }
    public void setTargetCompID(String targetCompID) { this.targetCompID = targetCompID; }
    
    public int getMsgSeqNum() { return msgSeqNum; }
    public void setMsgSeqNum(int msgSeqNum) { this.msgSeqNum = msgSeqNum; }
    
    public String getSendingTime() { return sendingTime; }
    public void setSendingTime(String sendingTime) { this.sendingTime = sendingTime; }
    
    public LocalDateTime getParsedTime() { return parsedTime; }
    public void setParsedTime(LocalDateTime parsedTime) { this.parsedTime = parsedTime; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }
    
    public double getOrderQty() { return orderQty; }
    public void setOrderQty(double orderQty) { this.orderQty = orderQty; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getOrdType() { return ordType; }
    public void setOrdType(String ordType) { this.ordType = ordType; }
    
    public String getTimeInForce() { return timeInForce; }
    public void setTimeInForce(String timeInForce) { this.timeInForce = timeInForce; }
    
    public String getClOrdID() { return clOrdID; }
    public void setClOrdID(String clOrdID) { this.clOrdID = clOrdID; }
    
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    
    public String getExecID() { return execID; }
    public void setExecID(String execID) { this.execID = execID; }
    
    public String getExecType() { return execType; }
    public void setExecType(String execType) { this.execType = execType; }
    
    public String getOrdStatus() { return ordStatus; }
    public void setOrdStatus(String ordStatus) { this.ordStatus = ordStatus; }
    
    public double getLastQty() { return lastQty; }
    public void setLastQty(double lastQty) { this.lastQty = lastQty; }
    
    public double getLastPx() { return lastPx; }
    public void setLastPx(double lastPx) { this.lastPx = lastPx; }
    
    public double getCumQty() { return cumQty; }
    public void setCumQty(double cumQty) { this.cumQty = cumQty; }
    
    public double getAvgPx() { return avgPx; }
    public void setAvgPx(double avgPx) { this.avgPx = avgPx; }
    
    public int getEncryptMethod() { return encryptMethod; }
    public void setEncryptMethod(int encryptMethod) { this.encryptMethod = encryptMethod; }
    
    public int getHeartBtInt() { return heartBtInt; }
    public void setHeartBtInt(int heartBtInt) { this.heartBtInt = heartBtInt; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public Map<String, String> getAllFields() { return allFields; }
    public void setAllFields(Map<String, String> allFields) { this.allFields = allFields; }
}
