# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NanoKafka is a toy Kafka implementation that supports S3 as storage. The project is built using Gradle with a multi-module structure and Java 21.

## Build System

- **Build Tool**: Gradle 8.14.1 with Kotlin DSL
- **Java Version**: Java 21 (managed via toolchain)
- **Dependency Management**: Version catalog (`gradle/libs.versions.toml`)

### Common Build Commands

```bash
# Build all modules
./gradlew build

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :lib:test
./gradlew :client:test
./gradlew :server:test

# Run a single test class
./gradlew :client:test --tests "io.github.unknowntpo.client.KafkaProducerTest"

# Build specific module JARs
./gradlew :client:jar
./gradlew :server:jar

# Run the server
./gradlew :server:run

# Or run the server JAR directly
java -jar server/build/libs/server-0.1.0.jar

# List all built JARs
find . -name "*.jar" -path "*/build/libs/*" | grep -v staging

# Clean build artifacts
./gradlew clean

# Publish client and server to Maven Central (requires GPG signing)
./gradlew :client:publishToMavenCentral
./gradlew :server:publishToMavenCentral

# Build without running tests
./gradlew build -x test
```

## Project Structure

```
nanoKafka/
├── lib/                    # Internal shared library (NOT published)
│   └── src/
│       ├── main/java/io/github/unknowntpo/
│       └── test/java/io/github/unknowntpo/
├── client/                 # Client library (PUBLISHED)
│   ├── build.gradle.kts
│   └── src/
│       ├── main/java/io/github/unknowntpo/client/
│       └── test/java/io/github/unknowntpo/client/
├── server/                 # Broker server (PUBLISHED)
│   ├── build.gradle.kts
│   └── src/
│       ├── main/java/io/github/unknowntpo/server/
│       └── test/java/io/github/unknowntpo/server/
└── build.gradle.kts       # Root build configuration
```

## Module Architecture

**lib** - Internal shared library (NOT published to Maven):
- Package: `io.github.unknowntpo`
- Contains core components: logSegment, record, topic (per plan.md)
- Used internally by client and server modules
- Output: `lib/build/libs/lib-0.1.0.jar`

**client** - Client library (PUBLISHED to Maven):
- Package: `io.github.unknowntpo.client`
- Published as `io.github.unknowntpo:nano-kafka-client:0.1.0`
- Contains Producer and Consumer APIs
- Depends on `lib` module
- Output: `client/build/libs/client-0.1.0.jar`, `client-0.1.0-sources.jar`

**server** - Broker server (PUBLISHED to Maven):
- Package: `io.github.unknowntpo.server`
- Published as `io.github.unknowntpo:nano-kafka-server:0.1.0`
- Main class: `io.github.unknowntpo.nanokafka.server.KafkaBroker`
- Depends on `lib` module for core components
- Builds as executable fat JAR with all dependencies bundled
- Output: `server/build/libs/server-0.1.0.jar` (7.9MB), `server-0.1.0-sources.jar`

## Publishing Configuration

Only `client` and `server` modules are published to Maven Central via Sonatype Central Portal:
- **Group ID**: `io.github.unknowntpo`
- **Client Artifact ID**: `nano-kafka-client`
- **Server Artifact ID**: `nano-kafka-server`
- **Version**: Defined in `gradle.properties` (currently `0.1.0`)
- **Signing**: Uses GPG command-line tool for artifact signing
- Uses `com.vanniktech.maven.publish` plugin for simplified Maven Central publishing

The `lib` module is NOT published - it's internal shared code only.

## Development Phases

As outlined in plan.md:

**Phase 1**: In-memory Kafka with logSegment, record, topic (no partition)
- Single producer → single consumer

**Phase 2**: On-disk persistence
- Save log segment to disk with index file
- Broker restart must persist data
- New consumers can read persisted records

## Key Dependencies

- **JUnit Jupiter**: Testing framework
- **Guava**: Internal utilities
- **Apache Commons Math3**: API dependency (exported to consumers)
