# Developer Guide

## Development Setup

### Prerequisites

- Java 17+
- Python 3.10+
- Maven 3.8+
- Docker & Docker Compose
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Environment Setup

1. **Clone Repository**
```bash
git clone https://github.com/Pratik44/FIX-SECURITY.git
cd FIX-SECURITY
```

2. **Start Infrastructure**
```bash
cd infrastructure
docker-compose -f docker-compose.dev.yml up -d
```

3. **Build Java Components**
```bash
cd fix-engine
mvn clean install
```

4. **Setup Python Environment**
```bash
cd api
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

## Project Structure

```
FIX_SECURITY/
├── fix-engine/          # Core FIX protocol engine
│   ├── src/main/java/
│   └── pom.xml
├── ingestion/           # Message ingestion layer
├── security-engine/      # Security monitoring
├── compliance/           # Compliance engine
├── analytics/            # Analytics and ML ## To be implemented
├── storage/              # Database schemas
├── api/                  # REST API
├── frontend/             # Web UI (planned)
├── infrastructure/       # Docker, K8s configs
└── docs/                 # Documentation
```

## Development Workflow

### Adding a New Feature

1. Create a feature branch:
```bash
git checkout -b feature/new-feature
```

2. Make your changes
3. Write tests
4. Run tests:
```bash
mvn test

pytest
```

5. Commit changes:
```bash
git add .
git commit -m "feat(scope): Add new feature"
```

6. Push and create PR

### Code Style

#### Java
- Use Google Java Style Guide
- Run `mvn checkstyle:check`
- Format code: `mvn formatter:format`

#### Python
- Follow PEP 8
- Use `black` for formatting
- Run `flake8` for linting

## Testing

### Unit Tests

**Java:**
```bash
cd fix-engine
mvn test
```

**Python:**
```bash
cd api
pytest tests/
```

### Integration Tests

Run integration tests with Docker:
```bash
docker-compose -f infrastructure/docker-compose.test.yml up --abort-on-container-exit
```

## Debugging

### Java Debugging

Run with debug port:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

Attach debugger to `localhost:5005`

### Python Debugging

Use VS Code debugger or add breakpoints:
```python
import pdb; pdb.set_trace()
```

## Building

### Build All Components

```bash
# Build Java
cd fix-engine
mvn clean package

# Build Docker images
docker build -t fix-security-engine:latest fix-engine/
docker build -t fix-security-api:latest api/
```

## Deployment

### Local Deployment

```bash
docker-compose -f infrastructure/docker-compose.prod.yml up -d
```


## Common Tasks

### Adding a New Compliance Rule

1. Create rule class in `compliance/src/main/java/com/fixsecurity/compliance/`
2. Extend `ComplianceRule` base class
3. Implement `matches()` and `evaluate()` methods
4. Register rule in `ComplianceRuleEngine`

### Adding a New API Endpoint

1. Add route in `api/app.py`
2. Add request/response models
3. Add tests
4. Update API documentation

### Adding a New Message Type

1. Update `FIXMessageParser.java` to handle new type
2. Add fields to `ParsedMessage.java`
3. Update tests
4. Update documentation

## Performance Optimization

- Use connection pooling for databases
- Implement caching with Redis
- Use async processing for heavy operations
- Optimize database queries with indexes


## Resources

- [FIX Protocol Specification](https://www.fixtrading.org/)
- [QuickFIX/J Documentation](https://www.quickfixj.org/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Flask Documentation](https://flask.palletsprojects.com/)
