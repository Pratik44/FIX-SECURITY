package com.fixsecurity.engine;

/**
 * Exception thrown when a FIX message cannot be parsed
 */
public class InvalidMessageException extends Exception {
    public InvalidMessageException(String message) {
        super(message);
    }
    
    public InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
