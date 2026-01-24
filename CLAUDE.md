# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

melosys-eessi is a microservice developed by Team Melosys that handles all EESSI (Electronic Exchange of Social Security Information) integration with EUX. It processes both incoming and outgoing SEDs (Structured Electronic Documents), handles journaling, case creation, and task management for A1 certificates and social security legislation.

## Build and Test Commands

```bash
# Build entire project
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run all unit tests
mvn test

# Run a single test class
mvn test -pl melosys-eessi-app -Dtest=SedMottakServiceTest

# Run a single test method
mvn test -pl melosys-eessi-app -Dtest=SedMottakServiceTest#testMethodName

# Run integration tests (requires Docker for Testcontainers)
mvn verify -pl melosys-eessi-test

# Run a specific integration test
mvn verify -pl melosys-eessi-test -Dit.test=SedSendtTestIT
```

## Running Locally

1. Start PostgreSQL:
   ```bash
   docker run -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=su -d --rm postgres
   ```

2. Run the application with `local-mock` profile:
   - Main class: `no.nav.melosys.eessi.MelosysEessiApplication`
   - Profile: `local-mock`

For local testing of SED reception, use the Swagger UI at http://localhost:8083/swagger-ui/ with the `lag-sed-controller`.

## Module Structure

- **melosys-eessi-app**: Main application module with all business logic
- **melosys-eessi-test**: Integration tests using Testcontainers (PostgreSQL)

## Architecture

### Kafka Integration
The service consumes and produces Kafka messages:

**Consumers** (`kafka/consumers/`):
- `SedMottattConsumer`: Receives incoming SEDs from EUX
- `SedSendtConsumer`: Processes sent SED confirmations
- `OppgaveHendelseConsumer`: Handles task (oppgave) events

**Producers** (`kafka/producers/`):
- `MelosysEessiAivenProducer`: Publishes EESSI messages to melosys-api

### External Integrations (`integration/`)
- **eux/**: EUX/RINA API for EESSI document exchange
- **pdl/**: Person Data Lake (GraphQL) for person lookup
- **journalpostapi/**: Joark for document archiving
- **oppgave/**: Task management service
- **saf/**: Document retrieval from archive
- **sak/**: Case management

### Core Processing Flows

**SED Reception** (see `diagrammer/SedMottak.md`):
1. Receive SED event
2. Check if already processed
3. Identify person (or create ID task if not identifiable)
4. Link to RINA case

**BUC Identification** (see `diagrammer/BucIdentifisert.md`):
1. On BUC identified event
2. Create journal posts for all received SEDs
3. Publish SED received messages

### SED Mappers (`service/sed/mapper/`)
- `til_sed/`: Mappers for creating outgoing SEDs (A001-A012, X-series)
- `fra_sed/`: Mappers for parsing incoming SEDs

## Tech Stack

- **Java 17 / Kotlin** (mixed codebase, migrating to Kotlin)
- **Spring Boot 3.5**
- **PostgreSQL** with Flyway migrations
- **Kafka** (Aiven) for messaging
- **Testcontainers** for integration tests

## Testing

- Unit tests: JUnit 5, Mockk (Kotlin), Kotest assertions
- Integration tests: Testcontainers with PostgreSQL
- Test data: `src/test/resources/mock/` contains sample SED JSON files

## Key Configuration

- Database migrations: `src/main/resources/db/migration/`
- Profiles: `local-mock` (local development), `local-q2` (connected to q2 environment)
- Feature toggles: Unleash
