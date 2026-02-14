package com.fixsecurity.engine;

import quickfix.*;
import quickfix.field.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * FIX Message Parser
 * Parses FIX protocol messages and extracts key information
 */
public class FIXMessageParser {
    
    /**
     * Parse a FIX message string into a structured object
     * 
     * @param fixString Raw FIX message string
     * @return ParsedMessage object containing parsed fields
     * @throws InvalidMessageException if message is invalid
     */
    public ParsedMessage parse(String fixString) throws InvalidMessageException {
        try {
            Message message = new Message(fixString);
            return extractMessageInfo(message);
        } catch (InvalidMessage e) {
            throw new InvalidMessageException("Failed to parse FIX message: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract key information from a FIX message
     */
    private ParsedMessage extractMessageInfo(Message message) {
        ParsedMessage parsed = new ParsedMessage();
        
        // Extract standard fields
        parsed.setMsgType(getStringField(message, MsgType.FIELD));
        parsed.setSenderCompID(getStringField(message, SenderCompID.FIELD));
        parsed.setTargetCompID(getStringField(message, TargetCompID.FIELD));
        parsed.setMsgSeqNum(getIntField(message, MsgSeqNum.FIELD));
        parsed.setSendingTime(getStringField(message, SendingTime.FIELD));
        
        // Extract message-specific fields based on type
        String msgType = parsed.getMsgType();
        
        if ("D".equals(msgType)) { // NewOrderSingle
            extractNewOrderFields(message, parsed);
        } else if ("8".equals(msgType)) { // ExecutionReport
            extractExecutionReportFields(message, parsed);
        } else if ("A".equals(msgType)) { // Logon
            extractLogonFields(message, parsed);
        } else if ("5".equals(msgType)) { // Logout
            extractLogoutFields(message, parsed);
        }
        
        // Extract all fields as key-value pairs
        parsed.setAllFields(extractAllFields(message));
        
        return parsed;
    }
    
    private void extractNewOrderFields(Message message, ParsedMessage parsed) {
        parsed.setSymbol(getStringField(message, Symbol.FIELD));
        parsed.setSide(getStringField(message, Side.FIELD));
        parsed.setOrderQty(getDoubleField(message, OrderQty.FIELD));
        parsed.setPrice(getDoubleField(message, Price.FIELD));
        parsed.setOrdType(getStringField(message, OrdType.FIELD));
        parsed.setTimeInForce(getStringField(message, TimeInForce.FIELD));
        parsed.setClOrdID(getStringField(message, ClOrdID.FIELD));
    }
    
    private void extractExecutionReportFields(Message message, ParsedMessage parsed) {
        parsed.setOrderID(getStringField(message, OrderID.FIELD));
        parsed.setExecID(getStringField(message, ExecID.FIELD));
        parsed.setExecType(getStringField(message, ExecType.FIELD));
        parsed.setOrdStatus(getStringField(message, OrdStatus.FIELD));
        parsed.setLastQty(getDoubleField(message, LastQty.FIELD));
        parsed.setLastPx(getDoubleField(message, LastPx.FIELD));
        parsed.setCumQty(getDoubleField(message, CumQty.FIELD));
        parsed.setAvgPx(getDoubleField(message, AvgPx.FIELD));
    }
    
    private void extractLogonFields(Message message, ParsedMessage parsed) {
        parsed.setEncryptMethod(getIntField(message, EncryptMethod.FIELD));
        parsed.setHeartBtInt(getIntField(message, HeartBtInt.FIELD));
        parsed.setUsername(getStringField(message, Username.FIELD));
    }
    
    private void extractLogoutFields(Message message, ParsedMessage parsed) {
        parsed.setText(getStringField(message, Text.FIELD));
    }
    
    private String getStringField(Message message, int fieldTag) {
        try {
            return message.getHeader().getString(fieldTag);
        } catch (FieldNotFound e) {
            try {
                return message.getString(fieldTag);
            } catch (FieldNotFound e2) {
                return null;
            }
        }
    }
    
    private int getIntField(Message message, int fieldTag) {
        try {
            return message.getHeader().getInt(fieldTag);
        } catch (FieldNotFound e) {
            try {
                return message.getInt(fieldTag);
            } catch (FieldNotFound e2) {
                return 0;
            }
        }
    }
    
    private double getDoubleField(Message message, int fieldTag) {
        try {
            return message.getHeader().getDouble(fieldTag);
        } catch (FieldNotFound e) {
            try {
                return message.getDouble(fieldTag);
            } catch (FieldNotFound e2) {
                return 0.0;
            }
        }
    }
    
    private Map<String, String> extractAllFields(Message message) {
        Map<String, String> fields = new HashMap<>();
        Iterator<Field<?>> headerIt = message.getHeader().iterator();
        while (headerIt.hasNext()) {
            Field<?> field = headerIt.next();
            fields.put(String.valueOf(field.getTag()), field.toString());
        }
        Iterator<Field<?>> bodyIt = message.iterator();
        while (bodyIt.hasNext()) {
            Field<?> field = bodyIt.next();
            fields.put(String.valueOf(field.getTag()), field.toString());
        }
        return fields;
    }
    
    /**
     * Validate FIX message checksum
     */
    public boolean validateChecksum(String fixString) {
        try {
            Message message = new Message(fixString);
            return message.getTrailer().getInt(CheckSum.FIELD) == 
                   MessageUtils.checksum(fixString.substring(0, fixString.lastIndexOf("10=")));
        } catch (Exception e) {
            return false;
        }
    }
}
