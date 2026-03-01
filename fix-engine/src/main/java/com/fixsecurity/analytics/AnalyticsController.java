package com.fixsecurity.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API for analytics reports: top anomalies and top order IDs by period (day, week, month).
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final int MAX_LIMIT = 1000;

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get top anomalies for the given period (newest first).
     * GET /api/analytics/reports/anomalies?period=day|week|month&limit=1000
     */
    @GetMapping("/reports/anomalies")
    public ResponseEntity<Map<String, Object>> getAnomalyReport(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "1000") int limit) {

        AnalyticsService.Period p = parsePeriod(period);
        int capped = Math.min(Math.max(1, limit), MAX_LIMIT);
        List<AnomalyRecord> anomalies = analyticsService.getTopAnomalies(p, capped);

        List<Map<String, Object>> items = anomalies.stream()
            .map(this::anomalyToMap)
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "period", p.name().toLowerCase(),
            "limit", capped,
            "count", items.size(),
            "anomalies", items
        ));
    }

    /**
     * Get top order IDs (by anomaly count) for the given period.
     * GET /api/analytics/reports/order-ids?period=day|week|month&limit=1000
     */
    @GetMapping("/reports/order-ids")
    public ResponseEntity<Map<String, Object>> getOrderIdReport(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "1000") int limit) {

        AnalyticsService.Period p = parsePeriod(period);
        int capped = Math.min(Math.max(1, limit), MAX_LIMIT);
        List<AnalyticsService.OrderIdReportItem> items = analyticsService.getTopOrderIds(p, capped);

        List<Map<String, Object>> list = items.stream()
            .map(item -> Map.<String, Object>of(
                "orderId", item.getOrderId(),
                "anomalyCount", item.getAnomalyCount()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "period", p.name().toLowerCase(),
            "limit", capped,
            "count", list.size(),
            "orderIds", list
        ));
    }

    /**
     * Record an anomaly (for integration with security engine / ingestion).
     * POST /api/analytics/anomalies
     * Body: { "anomalyType", "description", "orderId?", "clOrdID?", "sessionId?", "symbol?" }
     */
    @PostMapping("/anomalies")
    public ResponseEntity<Map<String, Object>> recordAnomaly(@RequestBody Map<String, String> body) {
        String type = body.get("anomalyType");
        String description = body.get("description");
        if (type == null || type.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "anomalyType is required"));
        }
        if (description == null) {
            description = "";
        }
        AnomalyRecord saved = analyticsService.recordAnomaly(
            type,
            description,
            body.get("orderId"),
            body.get("clOrdID"),
            body.get("sessionId"),
            body.get("symbol")
        );
        return ResponseEntity.ok(anomalyToMap(saved));
    }

    private Map<String, Object> anomalyToMap(AnomalyRecord r) {
        return Map.of(
            "id", r.getId(),
            "orderId", r.getOrderId() != null ? r.getOrderId() : "",
            "clOrdID", r.getClOrdID() != null ? r.getClOrdID() : "",
            "anomalyType", r.getAnomalyType(),
            "description", r.getDescription() != null ? r.getDescription() : "",
            "sessionId", r.getSessionId() != null ? r.getSessionId() : "",
            "symbol", r.getSymbol() != null ? r.getSymbol() : "",
            "createdAt", r.getCreatedAt().toString()
        );
    }

    private AnalyticsService.Period parsePeriod(String period) {
        if (period == null) return AnalyticsService.Period.DAY;
        switch (period.trim().toLowerCase()) {
            case "week":  return AnalyticsService.Period.WEEK;
            case "month": return AnalyticsService.Period.MONTH;
            default:       return AnalyticsService.Period.DAY;
        }
    }
}
