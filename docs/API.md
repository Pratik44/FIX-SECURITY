# FIX Security Platform API Documentation

## Base URL
```
http://localhost:5000/api/v1
```

## Authentication
Currently, the API does not require authentication. 

## Endpoints

### Health Check
**GET** `/health`

Check if the API is running.

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2026-02-11T10:00:00",
  "version": "1.0.0"
}
```

### Get Messages
**GET** `/messages`

Retrieve FIX messages with filtering options.

**Query Parameters:**
- `session_id` (string, optional): Filter by session ID
- `msg_type` (string, optional): Filter by message type (e.g., "D", "8")
- `start_time` (string, optional): Start time (ISO 8601 format)
- `end_time` (string, optional): End time (ISO 8601 format)
- `limit` (integer, optional): Maximum number of results (default: 100)
- `offset` (integer, optional): Offset for pagination (default: 0)

**Response:**
```json
{
  "messages": [
    {
      "id": "msg_1",
      "session_id": "SESSION-001",
      "msg_type": "D",
      "sender_comp_id": "SENDER",
      "target_comp_id": "TARGET",
      "timestamp": "2026-02-11T10:00:00",
      "symbol": "AAPL",
      "side": "1",
      "order_qty": 100.0,
      "price": 150.50
    }
  ],
  "total": 100,
  "limit": 100,
  "offset": 0
}
```

### Get Message by ID
**GET** `/messages/{message_id}`

Get a specific FIX message by its ID.

**Response:**
```json
{
  "id": "msg_1",
  "session_id": "SESSION-001",
  "msg_type": "D",
  "timestamp": "2026-02-11T10:00:00",
  ...
}
```

### Get Sessions
**GET** `/sessions`

Get all active FIX sessions.

**Response:**
```json
{
  "sessions": [
    {
      "session_id": "SESSION-001",
      "sender_comp_id": "SENDER",
      "target_comp_id": "TARGET",
      "message_count": 150,
      "last_message_time": "2026-02-11T10:00:00"
    }
  ],
  "total": 1
}
```

### Get Alerts
**GET** `/alerts`

Get security alerts.

**Query Parameters:**
- `severity` (string, optional): Filter by severity (CRITICAL, HIGH, MEDIUM, LOW)
- `start_time` (string, optional): Start time filter
- `limit` (integer, optional): Maximum results (default: 100)

**Response:**
```json
{
  "alerts": [
    {
      "alert_id": "alert_1",
      "session_id": "SESSION-001",
      "alert_type": "HIGH_MESSAGE_VOLUME",
      "severity": "HIGH",
      "description": "Unusual high message volume detected",
      "created_at": "2026-02-11T10:00:00"
    }
  ],
  "total": 10
}
```

### Get Compliance Status
**GET** `/compliance`

Get compliance evaluation results.

**Query Parameters:**
- `session_id` (string, optional): Filter by session ID

**Response:**
```json
{
  "compliance_rate": 95.5,
  "total_checks": 1000,
  "compliant_checks": 955,
  "violations": 45,
  "results": [
    {
      "result_id": "result_1",
      "session_id": "SESSION-001",
      "rule_id": "MIFID-II-001",
      "rule_name": "Best Execution Check",
      "compliant": true,
      "evaluation_message": "Price within acceptable range",
      "created_at": "2026-02-11T10:00:00"
    }
  ]
}
```

### Get Statistics
**GET** `/stats`

Get platform statistics.

**Response:**
```json
{
  "total_messages": 10000,
  "active_sessions": 5,
  "total_alerts": 25,
  "critical_alerts": 2,
  "message_type_distribution": {
    "D": 5000,
    "8": 3000,
    "A": 100,
    "5": 50
  },
  "timestamp": "2026-02-11T10:00:00"
}
```

### Create Message (Testing)
**POST** `/messages`

Create a test FIX message.

**Request Body:**
```json
{
  "session_id": "SESSION-001",
  "msg_type": "D",
  "sender_comp_id": "SENDER",
  "target_comp_id": "TARGET",
  "symbol": "AAPL",
  "side": "1",
  "order_qty": 100.0,
  "price": 150.50
}
```

**Response:**
```json
{
  "id": "msg_1",
  "timestamp": "2026-02-11T10:00:00",
  ...
}
```

## Error Responses

All errors follow this format:

```json
{
  "error": "Error message description"
}
```

**HTTP Status Codes:**
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `404` - Not Found
- `500` - Internal Server Error
