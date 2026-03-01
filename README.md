# FIX Protocol Security & Compliance Platform

A comprehensive security and compliance monitoring platform for Financial Information eXchange (FIX) protocol communications.

## Overview

This platform provides real-time monitoring, security analysis, compliance validation, and risk management for FIX protocol messages used in financial trading systems.

## Features

- ✅ **Real-time FIX Message Monitoring**: Capture, parse, and analyze FIX protocol messages
- ✅ **Security Threat Detection**: ML-based anomaly detection and threat identification
- ✅ **Regulatory Compliance**: Automated compliance checks for MiFID II, FINRA, SEC regulations
- ✅ **Risk Management**: Monitor trading patterns and detect market manipulation
- ✅ **Audit & Reporting**: Comprehensive audit trails and compliance reporting
- ✅ **REST API**: Full-featured API for integration and automation

## Architecture

```
┌─────────────────┐
│  FIX Messages   │
│  (Network/App)   │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│   Ingestion     │
│   (Kafka)       │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐     ┌──────────────┐
│  FIX Engine     │────▶│   Storage    │
│  (Parser)       │     │ (PostgreSQL, │
└────────┬─────────┘     │  InfluxDB)   │
         │               └──────────────┘
         ▼
┌─────────────────┐
│  Security       │
│  Engine         │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  Compliance     │
│  Engine         │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐
│  Analytics &    │
│  Reporting      │
└─────────────────┘
```

## Quick Start

### Prerequisites

- Java 17+
- Python 3.10+
- Docker & Docker Compose
- PostgreSQL 14+
- Apache Kafka
- Redis

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Pratik44/FIX-SECURITY.git
cd FIX-SECURITY
```

2. **Start infrastructure services**
```bash
docker-compose -f infrastructure/docker-compose.dev.yml up -d
```

3. **Build the project**
```bash
# Build Java components
cd fix-engine
mvn clean install

# Install Python dependencies
cd ../analytics
pip install -r requirements.txt
```

4. **Run the application**
```bash
# Start FIX Engine
cd fix-engine
mvn spring-boot:run

# Start API Server
cd ../api
python app.py
```

## Configuration

Configuration files are located in `infrastructure/config/`:
- `application.properties` - Main application configuration
- `kafka.properties` - Kafka connection settings
- `database.properties` - Database connection settings

## API Documentation

See [API Documentation](docs/API.md) for detailed API reference.

### Analytics Service (FIX Engine)

The FIX Engine exposes analytics endpoints for anomaly and order-ID reports by period (day, week, month), with a maximum of 1000 results per report:

- **GET /api/analytics/reports/anomalies?period=day|week|month&limit=1000** – Top anomalies (newest first) for the period.
- **GET /api/analytics/reports/order-ids?period=day|week|month&limit=1000** – Top order IDs (and clOrdIDs) by anomaly count for the period.
- **POST /api/analytics/anomalies** – Record an anomaly (JSON: `anomalyType`, `description`, optional `orderId`, `clOrdID`, `sessionId`, `symbol`).

## Documentation

- [Architecture Overview](docs/ARCHITECTURE.md)
- [Installation Guide](docs/INSTALLATION.md)
- [User Guide](docs/USER_GUIDE.md)
- [Developer Guide](docs/DEVELOPER_GUIDE.md)
- [API Reference](docs/API.md)
