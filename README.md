# SympAuthy

An open-source, self-hosted authorization server.

## Setting up the application for local development

### Requirements

- JDK 21
- [PostgreSQL](https://www.postgresql.org/)

### Create the application configuration

**FIXME**

### Launch the server

This project is a simple Java application built using [Gradle](https://gradle.org/).
You can launch it with any IDE supporting Gradle or directly using Gradle in the command line.

#### Gradle

```bash
MICRONAUT_CONFIG_FILES=$(pwd)/config/application.yml ./gradlew :core:run
```

#### IntelliJ

Add a new **Micronaut** configuration:
- **Name**: Application
- **Main class**: com.sympauthy.Application
- **Classpath**: sympauthy.server.main
- **Working directory**: $ProjectFileDir$
- **Environment variables**:
  - **MICRONAUT_CONFIG_FILES**: config/application.yml
