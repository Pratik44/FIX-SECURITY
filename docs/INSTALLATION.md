# Installation Guide

## Prereq

- **Java 17+** (OpenJDK or Oracle JDK)
- **Python 3.10+**
- **Docker** and **Docker Compose**
- **Maven 3.8+** (for Java builds)
- **Git**

## Step 1: Clone the Repository

```bash
git clone https://github.com/Pratik44/FIX-SECURITY.git
cd FIX-SECURITY
```

## Step 2: Start Infrastructure Services

Start all required infrastructure services using Docker Compose:

```bash
cd infrastructure
docker-compose -f docker-compose.dev.yml up -d
```

This will start:
- PostgreSQL (port 5432)
- InfluxDB (port 8086)
- Kafka (port 9092)
- Elasticsearch (port 9200)
- Redis (port 6379)

Verify services are running:
```bash
docker-compose -f docker-compose.dev.yml ps
```

## Step 3: Initialize Database

Connect to PostgreSQL and run the schema:

```bash
docker exec -i fix-security-postgres psql -U fixuser -d fix_security < ../storage/src/main/resources/schema.sql
```

## Step 4: Build Java Components

Build the FIX Engine:

```bash
cd fix-engine
mvn clean install
```

## Step 5: Install Python Dependencies

Install Python dependencies for the API:

```bash
cd api
pip install -r requirements.txt
```


## Step 6: Configure Application

Create configuration files:

**infrastructure/config/application.properties:**
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/fix_security
spring.datasource.username=fixuser
spring.datasource.password=fixpass

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=fix-security-group

# InfluxDB
influxdb.url=http://localhost:8086
influxdb.token=your-token
influxdb.org=fixsecurity
influxdb.bucket=fix_messages
```

## Step 7: Run the Application

### Start FIX Engine (Java)
```bash
cd fix-engine
mvn spring-boot:run
```

### Start API Server (Python)
```bash
cd api
python app.py
```

The API will be available at `http://localhost:5000`

## Step 8: Verify Installation

Test the health endpoint:

```bash
curl http://localhost:5000/api/v1/health
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2026-02-11T10:00:00",
  "version": "1.0.0"
}
```

## Troubleshooting

### Port Conflicts
If ports are already in use, modify `docker-compose.dev.yml` to use different ports.

### Database Connection Issues
Ensure PostgreSQL is running:
```bash
docker ps | grep postgres
```

### Kafka Connection Issues
Check Kafka logs:
```bash
docker logs fix-security-kafka
```

### Python Import Errors
Ensure all dependencies are installed:
```bash
pip install -r api/requirements.txt
```

## Next Steps

- Read the [User Guide](USER_GUIDE.md)
- Check [API Documentation](API.md)
- Review [Architecture Overview](ARCHITECTURE.md)
