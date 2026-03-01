package com.fixsecurity.analytics;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity for persisting anomaly events for analytics and reporting.
 */
@Entity
@Table(name = "anomaly_record", indexes = {
    @Index(name = "idx_anomaly_created_at", columnList = "created_at"),
    @Index(name = "idx_anomaly_order_id", columnList = "order_id"),
    @Index(name = "idx_anomaly_cl_ord_id", columnList = "cl_ord_id")
})
public class AnomalyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", length = 64)
    private String orderId;

    @Column(name = "cl_ord_id", length = 64)
    private String clOrdID;

    @Column(name = "anomaly_type", nullable = false, length = 64)
    private String anomalyType;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "session_id", length = 128)
    private String sessionId;

    @Column(name = "symbol", length = 32)
    private String symbol;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AnomalyRecord() {
        this.createdAt = Instant.now();
    }

    public AnomalyRecord(String anomalyType, String description) {
        this();
        this.anomalyType = anomalyType;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getClOrdID() { return clOrdID; }
    public void setClOrdID(String clOrdID) { this.clOrdID = clOrdID; }

    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
