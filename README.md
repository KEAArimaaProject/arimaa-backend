## Arimaa Backend

Simple Spring Boot backend with:
- Users API (create, get by id)
- MySQL → Neo4j modular migration (profile `migration`)

## Requirements

- Java 21
- Maven
- Databases running locally (defaults):
  - MySQL: `localhost:3307`, DB `arimaadockermysqldb`
  - MongoDB: `localhost:27017`, DB `arimaadockermysqldb`
  - Neo4j: `localhost:7687`

Connection settings are in `src/main/resources/application.properties`.

## Run the API

From the project root:

```bash
mvn spring-boot:run
```
or, if you use the project's Maven wrapper: 
```bash
.\mvnw.cmd spring-boot:run
```

Main endpoints:
- `POST /api/users`
- `GET /api/users/{id}`

## Run the SQL -> Neo4j / MongoDB migration

### 1) Start databases with Docker

From the project root:

```powershell
docker compose -f .\Database\docker-compose.mysql.yml up -d
docker compose -f .\Database\neo4j\docker-compose.neo4j.yml up -d
```

### 2) Apply Neo4j constraints once

Run [Database/neo4j/constraints.cypher](Database/neo4j/constraints.cypher) in Neo4j Browser or via `cypher-shell`.

### 3) Run migration in dry-run mode first

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=migration" "-Dspring-boot.run.arguments=--migration.dry-run=true"
```

### 4) Run the real migration

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=migration" "-Dspring-boot.run.arguments=--migration.dry-run=false"
```
You can still run from Bash (Git Bash, WSL, macOS, Linux):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=migration
```

PowerShell note: quote the `-D` flags so the shell does not strip `spring-boot` and break the argument (otherwise Maven may report `Unknown lifecycle phase ".run.profiles=migration"`):

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=migration"
```

Or set the profile in the environment, then run without `-D`:

```powershell
$env:SPRING_PROFILES_ACTIVE = "migration"
mvn spring-boot:run
```

This runs modular steps: orchestration in `com.example.arimaabackend.migration` (`MigrationRunner`, config, properties), each entity step in `com.example.arimaabackend.migration.steps` (countries, events, matches, moves, openings, `users` -> Neo4j, `user-mongo` -> MongoDB, etc.).

Control what runs with:
- `migration.enabled-targets[0]=neo4j` and/or `migration.enabled-targets[1]=mongodb`
- `migration.enabled-steps[0]=...`, `migration.enabled-steps[1]=...`

Set these keys in `src/main/resources/application-migration.properties` (or pass them via `-Dspring-boot.run.arguments`).

If either list is omitted, it defaults to `ALL` for that dimension.  
Use `migration.dry-run=true` to log counts without writing to target stores.

**Dependency order** (enforced by each step’s order value): country → event → game-type → position → puzzle → player → match → move → solution → opening-by-match → opening-by-puzzle → user. If you enable only a subset, ensure upstream nodes already exist in Neo4j.