-- FIX Security Platform Database Schema

-- Sessions table
CREATE TABLE IF NOT EXISTS fix_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    sender_comp_id VARCHAR(255) NOT NULL,
    target_comp_id VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages metadata table
CREATE TABLE IF NOT EXISTS fix_messages_metadata (
    message_id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    msg_type VARCHAR(10) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    direction VARCHAR(10) NOT NULL, -- INBOUND/OUTBOUND
    raw_message_hash VARCHAR(64),
    sender_comp_id VARCHAR(255),
    target_comp_id VARCHAR(255),
    msg_seq_num INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES fix_sessions(session_id)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_messages_session_id ON fix_messages_metadata(session_id);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON fix_messages_metadata(timestamp);
CREATE INDEX IF NOT EXISTS idx_messages_msg_type ON fix_messages_metadata(msg_type);
CREATE INDEX IF NOT EXISTS idx_messages_sender ON fix_messages_metadata(sender_comp_id);
CREATE INDEX IF NOT EXISTS idx_messages_target ON fix_messages_metadata(target_comp_id);

-- Security alerts table
CREATE TABLE IF NOT EXISTS security_alerts (
    alert_id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    message_id BIGINT,
    alert_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL, -- CRITICAL, HIGH, MEDIUM, LOW
    description TEXT NOT NULL,
    anomaly_details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    acknowledged BOOLEAN DEFAULT FALSE,
    acknowledged_at TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES fix_sessions(session_id),
    FOREIGN KEY (message_id) REFERENCES fix_messages_metadata(message_id)
);

CREATE INDEX IF NOT EXISTS idx_alerts_session ON security_alerts(session_id);
CREATE INDEX IF NOT EXISTS idx_alerts_severity ON security_alerts(severity);
CREATE INDEX IF NOT EXISTS idx_alerts_created ON security_alerts(created_at);

-- Compliance results table
CREATE TABLE IF NOT EXISTS compliance_results (
    result_id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    message_id BIGINT,
    rule_id VARCHAR(100) NOT NULL,
    rule_name VARCHAR(255) NOT NULL,
    compliant BOOLEAN NOT NULL,
    evaluation_message TEXT,
    evaluation_details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES fix_sessions(session_id),
    FOREIGN KEY (message_id) REFERENCES fix_messages_metadata(message_id)
);

CREATE INDEX IF NOT EXISTS idx_compliance_session ON compliance_results(session_id);
CREATE INDEX IF NOT EXISTS idx_compliance_rule ON compliance_results(rule_id);
CREATE INDEX IF NOT EXISTS idx_compliance_compliant ON compliance_results(compliant);

-- Audit log table
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    session_id VARCHAR(255),
    user_id VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    details JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES fix_sessions(session_id)
);

CREATE INDEX IF NOT EXISTS idx_audit_event_type ON audit_log(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_log(created_at);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_log(user_id);
