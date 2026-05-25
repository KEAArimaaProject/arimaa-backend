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

- From the project root:
restarts the database, perform the migration from mysql to mongo and neo4j
and finally run the program with .\mvnw.cmd spring-boot:run.
```
.\Database\scripts\prepare-and-start.ps1
```
(the script can randomly fail the first time, by "freezing". If it happens, just run it again).
- if you need it later: check that the 3 databases are running:
```
.\Database\scripts\check-databases.ps1
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
or
```
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=migration" "-Dspring-boot.run.arguments=--migration.dry-run=true"
```

### 4) Run the real migration

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=migration" "-Dspring-boot.run.arguments=--migration.dry-run=false"
```
or
```
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=migration" "-Dspring-boot.run.arguments=--migration.dry-run=false"
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

**Dependency order** (enforced by each step’s order value): country → event → game-type → position → puzzle → user → player → match → move → solution → opening-by-match → opening-by-puzzle. If you enable only a subset, ensure upstream nodes already exist in Neo4j.



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


### Debug migrations + Neo4j


1. Create a migration debugging run configuration:
    - Go to the top menu: **Run** > **Edit Configurations...**
    - Click the **+** (Add New Configuration) button in the top-left corner.
    - Select **Remote JVM Debug** from the list.
    - Name it: `Migration Debug (5005)`
    - In the configuration window, ensure these fields are set:
        - **Debugger mode**: `Attach to remote JVM`
        - **Host**: `localhost`
        - **Port**: `5005`
    - Click **OK** to save.
    - Keep the new run configuration for later.

2. Run debug script:  
- stop anything that is running (press `Ctrl+C` in the terminal)
- Run the debug script:
   ```powershell
   .\Database\scripts\debug-migration-neo4j.ps1 user false true
   ```
   (The first argument is the step name, the second is whether to dry-run, the third is whether to exit on complete).*
   (If you set the second argument to `true` (dry-run), the migration will skip the actual processing logic. To hit breakpoints inside the migration logic (e.g., in `toNode` or after the `dryRun` check), you MUST run it with `false`:

3. Use your new run configuration `Migration Debug (5005)` to attach the debugger:
   - You can now click on the debug  button next to the run configuration `Migration Debug (5005)`.

4. Identify Completion: Look for these messages in the terminal where you ran the script:
   - `Finished step 'user' in ... ms` (indicates the specific step finished).
   - `Data migration completed.` (indicates the entire runner is done).

5. you can now run the run the program: .\mvnw.cmd spring-boot:run
and run the neo4j tests.

### How to verify migration success
Besides looking at the logs, you can verify that the data was correctly written to Neo4j:
1. Open Neo4j Browser: Go to [http://localhost:7474/](http://localhost:7474/).
2. Check Node Counts: Run this Cypher query to see how many nodes were created:
   ```cypher
   MATCH (n) RETURN labels(n), count(n)
   ```
3. Inspect Specific Nodes: To see the migrated users:
   ```cypher
   MATCH (u:User) RETURN u LIMIT 25
   ```

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

### Identify issues with dataloading
first run these:
- docker compose --env-file .env -f .\Database\docker-compose.mysql.yml down -v --remove-orphans
- docker compose --env-file .env -f .\Database\docker-compose.mysql.yml up -d      

if they dont raise errors, then run this:
-  docker compose --env-file .env -f .\Database\docker-compose.mysql.yml logs -f --tail=50   

