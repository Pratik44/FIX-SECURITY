package com.fixsecurity.engine;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for FIX Engine - health, root, and parse endpoints.
 */
@RestController
@RequestMapping
public class FixEngineController {

    private final FIXMessageParser parser = new FIXMessageParser();

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> body = new HashMap<>();
        body.put("service", "FIX Protocol Engine");
        body.put("version", "1.0.0");
        body.put("status", "running");
        body.put("endpoints", Map.of(
            "health", "GET /health",
            "parse", "POST /api/parse (body: raw FIX message string)"
        ));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "healthy");
        body.put("timestamp", Instant.now().toString());
        body.put("version", "1.0.0");
        return ResponseEntity.ok(body);
    }

    /**
     * Parse a raw FIX message string and return extracted fields as JSON.
     * POST body: plain text FIX message (e.g. "8=FIX.4.4|35=D|49=...")
     */
    @PostMapping(value = "/api/parse", consumes = "text/plain", produces = "application/json")
    public ResponseEntity<?> parseFixMessage(@RequestBody String fixMessage) {
        if (fixMessage == null || fixMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body must contain a FIX message string"));
        }
        try {
            ParsedMessage parsed = parser.parse(fixMessage.trim());
            return ResponseEntity.ok(parsedToMap(parsed));
        } catch (InvalidMessageException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> parsedToMap(ParsedMessage p) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgType", p.getMsgType());
        m.put("senderCompID", p.getSenderCompID());
        m.put("targetCompID", p.getTargetCompID());
        m.put("msgSeqNum", p.getMsgSeqNum());
        m.put("sendingTime", p.getSendingTime());
        if (p.getSymbol() != null) m.put("symbol", p.getSymbol());
        if (p.getSide() != null) m.put("side", p.getSide());
        if (p.getOrderQty() != 0) m.put("orderQty", p.getOrderQty());
        if (p.getPrice() != 0) m.put("price", p.getPrice());
        if (p.getClOrdID() != null) m.put("clOrdID", p.getClOrdID());
        if (p.getOrderID() != null) m.put("orderID", p.getOrderID());
        if (p.getOrdStatus() != null) m.put("ordStatus", p.getOrdStatus());
        m.put("allFields", p.getAllFields());
        return m;
    }
}
