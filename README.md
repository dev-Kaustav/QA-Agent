# SEC Filings QA System

This repository contains a multi-module Maven project implementing a simplified SEC filings QA pipeline composed of six Spring Boot microservices built entirely on open source components.

## Modules
- **ingestion-service** – fetches filings and stores raw HTML to MinIO.
- **parser-service** – parses HTML from MinIO into text.
- **chunker-service** – splits parsed text into logical sections and stores them in PostgreSQL.
- **embedding-service** – creates embeddings locally using an open-source tokenizer and indexes them into Elasticsearch.
- **retrieval-service** – retrieves relevant sections based on vector similarity and metadata filters.
- **qa-service** – generates answers with a lightweight open-source model.

## Building
```bash
mvn clean package
```

## Running with Docker Compose
Ensure Docker and Docker Compose are installed, then run:
```bash
docker compose up --build
```
This will start supporting infrastructure (Kafka, Zookeeper, PostgreSQL, MinIO, Elasticsearch) along with all services.

## Testing
Unit tests can be executed with:
```bash
mvn test
```
