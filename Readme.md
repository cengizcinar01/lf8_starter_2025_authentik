# LF08 Projekt: Project-Management-Service

Dieses Projekt implementiert ein Backend für eine Projektverwaltungssoftware als Microservice.

Der Service bietet eine REST-Schnittstelle zur Verwaltung von Projekten und deren zugeordneten Mitarbeitern. Die Authentifizierung erfolgt über einen zentralen Authentik-Service mittels JWT.

## Technologie-Stack
- Java 17 & Spring Boot 3.3
- PostgreSQL
- REST-API mit JSON
- JWT-Authentifizierung (via Authentik)
- Docker & Docker Compose
- OpenAPI / Swagger zur Dokumentation
- Gradle
- JUnit 5 & Testcontainers für Integrationstests

## Requirements
* Docker https://docs.docker.com/get-docker/
* Docker compose (bei Windows und Mac schon in Docker enthalten) https://docs.docker.com/compose/install/

## Endpunkte
- **Swagger-UI:** `http://localhost:8080/swagger`
- **Basis-URL:** `http://localhost:8080`

### Haupt-Endpunkte
- `POST /projects`: Erstellt ein neues Projekt.
- `GET /projects`: Ruft alle Projekte ab.
- `GET /projects/{id}`: Ruft ein spezifisches Projekt ab.
- `PUT /projects/{id}`: Aktualisiert ein Projekt.
- `DELETE /projects/{id}`: Löscht ein Projekt.

### Mitarbeiter-Management in Projekten
- `POST /projects/{projectId}/employees`: Fügt einen Mitarbeiter zu einem Projekt hinzu.
- `DELETE /projects/{projectId}/employees/{employeeId}`: Entfernt einen Mitarbeiter aus einem Projekt.
- `GET /projects/{projectId}/employees`: Ruft alle Mitarbeiter eines Projekts ab.
- `GET /projects/employees/{employeeId}/projects`: Ruft alle Projekte eines Mitarbeiters ab.


# Postgres

### Postgres und Authentik starten
```bash
docker compose up
```
Achtung: Der Docker-Container läuft dauerhaft! Wenn er nicht mehr benötigt wird, sollten Sie ihn stoppen.

### Postgres stoppen
```bash
docker compose down
```

### Postgres Datenbank wipen, z.B. bei Problemen
```bash
docker compose down
docker volume rm local_lf8_starter_postgres_data
docker compose up
```

### Intellij-Ansicht für Postgres Datenbank einrichten
```bash
1. Lasse den Docker-Container mit der PostgreSQL-Datenbank laufen
2. im Ordner resources die Datei application.properties öffnen und die URL der Datenbank kopieren
3. rechts im Fenster den Reiter Database öffnen
4. In der Database-Symbolleiste auf das Datenbanksymbol mit dem Schlüssel klicken
5. auf das Pluszeichen klicken
6. Datasource from URL auswählen
7. URL der DB einfügen und PostgreSQL-Treiber auswählen, mit OK bestätigen
8. Username lf8_starter und Passwort secret eintragen (siehe application.properties), mit Apply bestätigen
9. im Reiter Schemas alle Häkchen entfernen und lediglich vor lf8_starter_db und public Häkchen setzen
10. mit Apply und ok bestätigen 
```
# Authentik 

### JWT Token
Um einen JWT Token zu generieren, der für die Authentifizierung benötigt wird, gehen Sie wie folgt vor:
1. Auf der Projektebene [0_auth.http](http/0_auth.http) öffnen.
2. Neben der Request auf den grünen Pfeil drücken
3. Aus dem Reponse das access_token kopieren