package com.fixsecurity.analytics;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics service for anomaly and order-id reports by period (day, week, month).
 */
@Service
public class AnalyticsService {

    private static final int MAX_LIMIT = 1000;

    private final AnomalyRecordRepository anomalyRecordRepository;

    public AnalyticsService(AnomalyRecordRepository anomalyRecordRepository) {
        this.anomalyRecordRepository = anomalyRecordRepository;
    }

    public enum Period {
        DAY,
        WEEK,
        MONTH
    }

    /**
     * Returns the time range [start, end] for the given period ending at (or including) now.
     */
    public static java.time.temporal.TemporalAmount durationFor(Period period) {
        switch (period) {
            case DAY:   return ChronoUnit.DAYS.getDuration();
            case WEEK:  return ChronoUnit.WEEKS.getDuration();
            case MONTH: return ChronoUnit.DAYS.getDuration().multipliedBy(30);
            default:    return ChronoUnit.DAYS.getDuration();
        }
    }

    private static Instant[] toRange(Period period) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Instant end = now.toInstant();
        Instant start;
        switch (period) {
            case DAY:
                start = now.truncatedTo(ChronoUnit.DAYS).toInstant();
                break;
            case WEEK:
                start = now.truncatedTo(ChronoUnit.DAYS).minusWeeks(1).toInstant();
                break;
            case MONTH:
                start = now.truncatedTo(ChronoUnit.DAYS).minusMonths(1).toInstant();
                break;
            default:
                start = now.truncatedTo(ChronoUnit.DAYS).toInstant();
        }
        return new Instant[] { start, end };
    }

    /**
     * Get top anomalies (newest first) for the given period, up to limit (capped at 1000).
     */
    public List<AnomalyRecord> getTopAnomalies(Period period, int limit) {
        int capped = Math.min(Math.max(1, limit), MAX_LIMIT);
        Instant[] range = toRange(period);
        return anomalyRecordRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(
            range[0], range[1], PageRequest.of(0, capped));
    }

    /**
     * Get top order IDs (and clOrdIDs) by anomaly count for the given period, up to limit (capped at 1000).
     * Combines order_id and cl_ord_id into a single list of "order identifiers" with counts.
     */
    public List<OrderIdReportItem> getTopOrderIds(Period period, int limit) {
        int capped = Math.min(Math.max(1, limit), MAX_LIMIT);
        Instant[] range = toRange(period);

        List<Object[]> byOrderId = anomalyRecordRepository.findTopOrderIdsByAnomalyCount(range[0], range[1]);
        List<Object[]> byClOrdId = anomalyRecordRepository.findTopClOrdIdsByAnomalyCount(range[0], range[1]);

        Map<String, Long> combined = new LinkedHashMap<>();
        for (Object[] row : byOrderId) {
            String id = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            combined.merge(id, count, Long::sum);
        }
        for (Object[] row : byClOrdId) {
            String id = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            combined.merge(id, count, Long::sum);
        }

        return combined.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(capped)
            .map(e -> new OrderIdReportItem(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Record an anomaly for analytics (e.g. from security engine).
     */
    @Transactional
    public AnomalyRecord recordAnomaly(String anomalyType, String description,
                                       String orderId, String clOrdID, String sessionId, String symbol) {
        AnomalyRecord r = new AnomalyRecord(anomalyType, description);
        r.setOrderId(orderId);
        r.setClOrdID(clOrdID);
        r.setSessionId(sessionId);
        r.setSymbol(symbol);
        return anomalyRecordRepository.save(r);
    }

    /**
     * DTO for order ID report: identifier (orderId or clOrdID) and anomaly count.
     */
    public static class OrderIdReportItem {
        private String orderId;
        private long anomalyCount;

        public OrderIdReportItem(String orderId, long anomalyCount) {
            this.orderId = orderId;
            this.anomalyCount = anomalyCount;
        }

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public long getAnomalyCount() { return anomalyCount; }
        public void setAnomalyCount(long anomalyCount) { this.anomalyCount = anomalyCount; }
    }
}
