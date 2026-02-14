# User Guide

## Overview

This guide will help you get started with using the FIX Security Platform.

## Quick Start

### 1. Start the Platform

```bash
# Start infrastructure
cd infrastructure
docker-compose -f docker-compose.dev.yml up -d

# Start FIX Engine
cd ../fix-engine
mvn spring-boot:run

# Start API Server
cd ../api
python app.py
```

### 2. Access the API

The API is available at `http://localhost:5000`

### 3. Check Health

```bash
curl http://localhost:5000/api/v1/health
```

## Key Features

### Message Monitoring

Query FIX messages:

```bash
# Get all messages
curl http://localhost:5000/api/v1/messages

# Filter by session
curl http://localhost:5000/api/v1/messages?session_id=SESSION-001

# Filter by message type
curl http://localhost:5000/api/v1/messages?msg_type=D
```

### Session Management

View active sessions:

```bash
curl http://localhost:5000/api/v1/sessions
```

### Security Alerts

View security alerts:

```bash
# Get all alerts
curl http://localhost:5000/api/v1/alerts

# Filter by severity
curl http://localhost:5000/api/v1/alerts?severity=CRITICAL
```

### Compliance Status

Check compliance status:

```bash
# Overall compliance
curl http://localhost:5000/api/v1/compliance

# Session-specific compliance
curl http://localhost:5000/api/v1/compliance?session_id=SESSION-001
```

### Statistics

View platform statistics:

```bash
curl http://localhost:5000/api/v1/stats
```

## Integration Examples

### Python

```python
import requests

base_url = "http://localhost:5000/api/v1"

# Get messages
response = requests.get(f"{base_url}/messages", params={
    "session_id": "SESSION-001",
    "limit": 100
})
messages = response.json()

# Get alerts
response = requests.get(f"{base_url}/alerts", params={
    "severity": "CRITICAL"
})
alerts = response.json()
```

### JavaScript/Node.js

```javascript
const axios = require('axios');

const baseUrl = 'http://localhost:5000/api/v1';

// Get messages
axios.get(`${baseUrl}/messages`, {
    params: {
        session_id: 'SESSION-001',
        limit: 100
    }
}).then(response => {
    console.log(response.data);
});

// Get alerts
axios.get(`${baseUrl}/alerts`, {
    params: { severity: 'CRITICAL' }
}).then(response => {
    console.log(response.data);
});
```

## Best Practices

1. **Monitor Regularly**: Check alerts and compliance status regularly
2. **Set Up Alerts**: Configure alerting for critical security events
3. **Review Logs**: Regularly review audit logs for suspicious activity
4. **Update Rules**: Keep compliance rules up to date with regulations
5. **Backup Data**: Regularly backup database and configuration

## Troubleshooting

### API Not Responding
- Check if services are running: `docker ps`
- Check API logs: `docker logs fix-security-api`
- Verify port 5000 is not in use

### No Messages Appearing
- Verify Kafka is running: `docker logs fix-security-kafka`
- Check FIX Engine logs for parsing errors
- Verify network capture is configured correctly

### Database Connection Issues
- Ensure PostgreSQL is running: `docker ps | grep postgres`
- Check connection settings in `application.properties`
- Verify database schema is initialized

## Support

For issues or questions:
- Open an issue on GitHub
- Email: pratik.deenbandhu456@gmail.com
- Check documentation in `docs/` directory
