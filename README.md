# Kafka Spring Boot POC - Event-Driven Microservices

A comprehensive Proof of Concept demonstrating Apache Kafka integration with Spring Boot for building scalable, event-driven microservices architecture.

## 🏗️ System Architecture

### Core Components
- **Producer Service**: HTTP REST API for publishing events to Kafka topics
- **Consumer Service**: Event processing and message consumption from Kafka topics
- **Kafka Streams Service**: Real-time stream processing and data transformation
- **Schema Registry**: Centralized schema management for data serialization
- **Monitoring Stack**: Comprehensive observability and metrics collection

## 🚀 Features & Functionality

### 1. Producer Service (`producer-app`)

#### Message Publishing
- **REST API Endpoints**: HTTP interfaces for publishing messages
- **Multiple Message Types**: Support for String, JSON, Avro, and custom serialization
- **Batch Publishing**: Bulk message publishing for high throughput
- **Async Publishing**: Non-blocking message publishing with callbacks
- **Message Headers**: Custom headers and metadata support

#### Error Handling & Reliability
- **Retry Mechanism**: Configurable retry policies with exponential backoff
- **Dead Letter Topics**: Failed message routing and handling
- **Idempotent Publishing**: Prevent duplicate message publishing
- **Transaction Support**: Atomic message publishing across multiple topics
- **Circuit Breaker**: Fault tolerance for downstream dependencies

#### Performance & Monitoring
- **Connection Pooling**: Optimized Kafka producer connections
- **Compression**: Message compression (gzip, snappy, lz4, zstd)
- **Batch Configuration**: Tunable batch size and linger time
- **Metrics Collection**: Producer metrics and performance monitoring
- **Health Checks**: Service health endpoints and readiness probes

### 2. Consumer Service (`consumer-app`)

#### Message Consumption
- **@KafkaListener**: Annotation-based message consumption
- **Consumer Groups**: Load balancing across multiple consumer instances
- **Multiple Topics**: Subscribe to multiple topics with different handlers
- **Message Filtering**: Content-based message filtering
- **Parallel Processing**: Concurrent message processing within partitions

#### Offset Management
- **Auto Commit**: Automatic offset commitment
- **Manual Commit**: Manual offset control for at-least-once processing
- **Commit Strategies**: Batch, record-level, and time-based commits
- **Offset Reset**: Configurable offset reset strategies
- **Seek Operations**: Manual partition seeking and replay capabilities

#### Error Handling & Recovery
- **Error Topics**: Routing failed messages to error topics
- **Retry Topics**: Implementing retry mechanisms with delay
- **Skip Strategies**: Configurable error skipping policies
- **Dead Letter Queues**: Final destination for unprocessable messages
- **Graceful Shutdown**: Proper consumer shutdown and resource cleanup

### 3. Kafka Streams Service (`streams-app`)

#### Stream Processing
- **Real-time Transformations**: Map, filter, flatMap operations
- **Stateless Processing**: Transformations without state storage
- **Branching**: Split streams based on conditions
- **Merging**: Combine multiple streams into one
- **Custom Processors**: Low-level processor API for complex logic

#### Stateful Operations
- **Aggregations**: Count, sum, average, and custom aggregations
- **Windowing**: Time-based, session-based, and hopping windows
- **Joins**: Stream-stream and stream-table joins
- **State Stores**: Local state management and querying
- **Interactive Queries**: REST API for querying stream state

#### Advanced Features
- **Exactly-Once Processing**: Transactional stream processing
- **Custom Serdes**: Domain-specific serialization/deserialization
- **Topology Optimization**: Stream topology optimization
- **Error Handling**: Stream processing error handling and recovery
- **Rebalancing**: Dynamic partition rebalancing

### 4. Schema Management

#### Schema Registry Integration
- **Avro Schemas**: Structured data serialization with evolution
- **Schema Evolution**: Forward and backward compatibility
- **Schema Validation**: Runtime schema validation
- **Version Management**: Schema versioning and migration
- **Subject Strategies**: Different schema subject naming strategies

#### Serialization Support
- **JSON Serialization**: Standard JSON with schema validation
- **Avro Serialization**: Binary serialization with schema evolution
- **Protobuf Support**: Protocol buffers integration
- **Custom Serializers**: Domain-specific serialization logic
- **Compression**: Schema-aware compression

### 5. Security Features

#### Authentication & Authorization
- **SASL Authentication**: PLAIN, SCRAM-SHA-256, SCRAM-SHA-512
- **SSL/TLS Encryption**: End-to-end encryption
- **ACL Management**: Topic and consumer group access controls
- **OAuth2 Integration**: Modern authentication mechanisms
- **Certificate Management**: SSL certificate handling

#### Data Protection
- **Message Encryption**: Application-level message encryption
- **PII Handling**: Personal data anonymization and masking
- **Audit Logging**: Security event logging and monitoring
- **Data Retention**: Configurable data retention policies
- **Compliance**: GDPR and data compliance features

### 6. Monitoring & Observability

#### Metrics Collection
- **JMX Metrics**: Native Kafka producer/consumer metrics
- **Custom Metrics**: Application-specific business metrics
- **Micrometer Integration**: Metrics collection with Spring Boot Actuator
- **Prometheus Export**: Metrics export for Prometheus
- **Performance Tracking**: Throughput, latency, and error rate tracking

#### Logging & Tracing
- **Structured Logging**: JSON-formatted application logs
- **Correlation IDs**: Request tracing across services
- **Distributed Tracing**: Zipkin/Jaeger integration
- **Kafka Message Tracing**: End-to-end message tracking
- **Error Logging**: Comprehensive error logging and alerting

#### Dashboards & Alerting
- **Grafana Dashboards**: Real-time monitoring dashboards
- **Alerting Rules**: Automated alerting for critical metrics
- **Health Endpoints**: Service health and readiness checks
- **Log Aggregation**: Centralized logging with ELK stack
- **Performance Reports**: Automated performance reporting

### 7. Testing & Quality

#### Testing Strategy
- **Unit Tests**: Comprehensive unit test coverage
- **Integration Tests**: End-to-end integration testing
- **TestContainers**: Docker-based testing with real Kafka
- **Contract Testing**: Producer-consumer contract verification
- **Load Testing**: Performance and scalability testing

#### Test Utilities
- **Embedded Kafka**: In-memory Kafka for testing
- **Test Data Generation**: Automated test data creation
- **Mock Producers/Consumers**: Test doubles for isolated testing
- **Test Assertions**: Kafka-specific test assertions
- **Test Configuration**: Environment-specific test configurations

## 🛠️ Technology Stack

### Core Technologies
- **Java 17+**: Modern Java features and performance
- **Spring Boot 3.x**: Latest Spring Boot framework
- **Apache Kafka 3.x**: Latest Kafka with KRaft support
- **Spring Kafka**: Spring's Kafka integration library
- **Kafka Streams**: Stream processing library

### Supporting Technologies
- **Docker**: Containerization and deployment
- **Docker Compose**: Local development environment
- **Gradle**: Build automation and dependency management
- **Lombok**: Code generation and boilerplate reduction
- **Jackson**: JSON serialization/deserialization

### Infrastructure
- **Confluent Schema Registry**: Schema management
- **Kafka Connect**: Data integration platform
- **Prometheus**: Metrics collection and monitoring
- **Grafana**: Metrics visualization and dashboards
- **Zipkin**: Distributed tracing

## 📦 Project Structure

```
kafka-spring-poc/
├── modules/
│   ├── producer-app/          # Producer service
│   ├── consumer-app/          # Consumer service
│   ├── streams-app/           # Kafka Streams service
│   └── common/                # Shared utilities and models
├── docker/
│   ├── docker-compose.yml     # Local development environment
│   └── kafka-cluster/         # Multi-broker Kafka setup
├── monitoring/
│   ├── grafana/               # Grafana dashboards
│   └── prometheus/            # Prometheus configuration
└── scripts/
    ├── setup.sh               # Environment setup
    └── performance-test.sh    # Performance testing
```

## 🚦 Implementation Status

### ✅ Completed Features
- [x] Basic Producer Service with REST API
- [x] KafkaTemplate configuration and dependency injection
- [x] Basic message publishing functionality
- [x] Health check endpoints
- [x] Project structure and build configuration

### 🔄 In Development
- [ ] Consumer Service implementation
- [ ] Docker Compose environment setup
- [ ] Schema Registry integration
- [ ] Error handling and retry mechanisms
- [ ] Basic monitoring and metrics

### 📋 Planned Features
- [ ] Kafka Streams service
- [ ] Advanced security features
- [ ] Comprehensive monitoring stack
- [ ] Performance optimization
- [ ] Production deployment configurations

## 🎯 Business Use Cases

### Event-Driven Architecture
- **Order Processing**: Order events processing across microservices
- **User Activity Tracking**: Real-time user behavior analytics
- **Inventory Management**: Stock level updates and notifications
- **Payment Processing**: Payment event handling and reconciliation

### Data Integration
- **Database CDC**: Change Data Capture from databases
- **External API Integration**: Third-party service integration
- **Data Synchronization**: Multi-system data synchronization
- **ETL Pipelines**: Extract, Transform, Load operations

### Real-time Analytics
- **Stream Processing**: Real-time data transformation
- **Aggregations**: Live metrics and KPI calculations
- **Fraud Detection**: Real-time fraud analysis
- **Recommendation Engine**: Live recommendation updates

---

*Status: Active Development*
*Last Updated: September 21, 2025*