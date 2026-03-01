package com.fixsecurity.analytics;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * Repository for querying anomaly records for analytics reports.
 */
public interface AnomalyRecordRepository extends JpaRepository<AnomalyRecord, Long> {

    /**
     * Find anomaly records within a time range, newest first, limited by page size.
     */
    List<AnomalyRecord> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to, Pageable pageable);

    /**
     * Get order IDs with the most anomalies in a time range (up to 1000).
     */
    @Query(value = """
        SELECT r.order_id, COUNT(*) AS cnt
        FROM anomaly_record r
        WHERE r.created_at BETWEEN :from AND :to
          AND r.order_id IS NOT NULL AND r.order_id != ''
        GROUP BY r.order_id
        ORDER BY cnt DESC
        LIMIT 1000
        """, nativeQuery = true)
    List<Object[]> findTopOrderIdsByAnomalyCount(@Param("from") Instant from, @Param("to") Instant to);

    /**
     * Same for cl_ord_id when order_id is not present (e.g. NewOrderSingle before execution).
     */
    @Query(value = """
        SELECT r.cl_ord_id, COUNT(*) AS cnt
        FROM anomaly_record r
        WHERE r.created_at BETWEEN :from AND :to
          AND r.cl_ord_id IS NOT NULL AND r.cl_ord_id != ''
        GROUP BY r.cl_ord_id
        ORDER BY cnt DESC
        LIMIT 1000
        """, nativeQuery = true)
    List<Object[]> findTopClOrdIdsByAnomalyCount(@Param("from") Instant from, @Param("to") Instant to);
}
