# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-11

### Added
- Initial release of FIX Security Platform
- FIX message parser supporting FIX 4.2, 4.4, 5.0
- Session management and monitoring
- Kafka-based message ingestion
- Security anomaly detection engine
- Compliance rule engine with pre-built rules
- REST API for querying messages, sessions, alerts, and compliance status
- Docker Compose setup for local development
- PostgreSQL database schema
- Comprehensive documentation (README, API docs, Installation guide, Architecture docs)
- Support for multiple message types (NewOrderSingle, ExecutionReport, Logon, Logout)

### Infrastructure
- Docker Compose configuration for:
  - PostgreSQL 14
  - InfluxDB 2.0
  - Apache Kafka
  - Elasticsearch 8.8
  - Redis 7

### Documentation
- README with overview and quick start
- API documentation
- Installation guide
- Architecture overview
- User guide
- Developer guide
- Contributing guidelines

## [Unreleased]

### Planned Features
- Web UI dashboard
- Machine learning models for advanced anomaly detection
- Additional compliance rules (MiFID II, FINRA, SEC)
- Multi-tenancy support
- Authentication and authorization
- Advanced reporting and analytics
- Kubernetes deployment configurations
- Performance monitoring and metrics
- Webhook support for integrations
