## Arimaa Backend

Simple Spring Boot backend with:
- Users API (create, get by id)
- MySQL → Neo4j modular migration (profile `migration`)

## Requirements

- a secrets file (.env), to be placed in the project root. Get it from the team.
- Java 21
- Maven
- Databases running locally (defaults): see the .env file (not in git)

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
docker compose --env-file .env -f .\Database\docker-compose.mysql.yml up -d
docker compose --env-file .env -f .\Database\neo4j\docker-compose.neo4j.yml up -d
```

### 1B) Check that the mysql database is running correctly.
```powershell
docker compose --env-file .env -f .\Database\docker-compose.mysql.yml ps
```
You should see information about the database.



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



### to work with neo4J

RUn the docker compose script  above.

to go http://localhost:7474/ in a  borwser

login with the neo4j password (see the .env file (not in git))


Check on the left side in the neo4j browser window, if there is data:
If you see this:
"Database information
Nodes (0)"
Then you need to run the migration script:

PS C:\Users\CMLyk\IdeaProjects\arimaa-backend> .\mvnw.cmd  spring-boot:run "-Dspring-boot.run.profiles=migration"

--- To delete the database:
docker compose --env-file .env -f .\Database\neo4j\docker-compose.neo4j.yml down -v

to delete mysql database:
docker compose --env-file .env -f .\Database\docker-compose.mysql.yml down -v


### Run Endpoints in postman:
- run docker desktop

- remove database: docker compose --env-file .env -f .\Database\docker-compose.mysql.yml down -v
- add dataabase: docker compose --env-file .env -f .\Database\docker-compose.mysql.yml up -d

the down command might fail by "hanging" on windows. If so, try pressing Ctrl+c and running this:
docker compose --env-file .env -f .\Database\docker-compose.mysql.yml down -v --remove-orphans

verify MySQL: docker compose --env-file .env -f Database\docker-compose.mysql.yml ps

- start the application: .\mvnw.cmd spring-boot:run

- you can also try this online script:
docker compose --env-file .env -f .\Database\docker-compose.mysql.yml down -v --remove-orphans; docker compose --env-file .env -f .\Database\docker-compose.mysql.yml up -d ; sleep 20; .\mvnw.cmd spring-boot:run

### Open the database in datagrip:
- Open docker desktop.
- Make sure the database is running (use the guides above), then verify with
  docker compose --env-file .env -f .\Database\docker-compose.mysql.yml ps

Host: localhost
Port: 5000
Database: arimaadockermysqldb
User: root
Password: <MYSQL_ROOT_PASSWORD from .env>
JDBC URL: jdbc:mysql://localhost:5000/arimaadockermysqldb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC


### Debug playwright tests
To debug the playwright tests here:
src/test/java/com/example/arimaabackend/playwright

make sure that nothing is running on port 8080
(in the terminal, press ctrl+C to close .\mvnw.cmd spring-boot:run which might be running)

Then go to
src/main/java/com/example/arimaabackend/ArimaaBackendApplication.java
and run in debug mode.

you can now debug the playwright tests and hit breakpoints in the services.
eg.
src/test/java/com/example/arimaabackend/playwright/MatchTest.java
void AsUser_UpdateMatch() throws JsonProcessingException {


