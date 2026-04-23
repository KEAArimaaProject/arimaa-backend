## Arimaa Backend

Simple Spring Boot backend with:
- Users API (create, get by id)
- MySQL → MongoDB + Neo4j migration

## Requirements

- Java 21
- Maven
- Databases running locally (defaults):
  - MySQL: `localhost:3306`, DB `arimaa`
  - MongoDB: `localhost:27017`, DB `arimaa`
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

## Run the migration

From the project root:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=migration
```

This reads all users from MySQL and writes them to MongoDB and Neo4j.