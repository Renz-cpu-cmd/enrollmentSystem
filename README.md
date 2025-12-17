# Enrollment Desktop App (Swing)

Java 17 Swing desktop app using SQLite (HikariCP). This is not Spring Boot.

## Build & Run
- Build a runnable JAR:
	- `mvn clean package`
- Run from the repo root (so SQLite path resolves):
	- If a shaded jar is created: `java -jar target/demo-0.0.1-SNAPSHOT-shaded.jar`
	- Otherwise: `java -jar target/demo-0.0.1-SNAPSHOT.jar`
	- Tip: list target to confirm the exact JAR name: `dir target` (Windows)

## Database migrations (Flyway)
- Migrations live in `src/main/resources/db/migration`.
- The app runs Flyway at startup (via `DatabaseMigrator.runMigrations()` in `Main`).
- Manual/CI example (SQLite):
  - `mvn -Dflyway.url=jdbc:sqlite:enrollment.db -Dflyway.driver=org.sqlite.JDBC -Dflyway.locations=filesystem:src/main/resources/db/migration flyway:migrate`
  - Override `-Dflyway.url` in CI to target your environment.

## Schema snapshot
- students(id, student_id unique, email unique, password hash, profile fields)
- courses(id, code unique, name, description, units, schedule)
- enrollments(id, student_id FK, course_id FK, academic_year, term, status)
- payments(id, enrollment_id FK, amount, method, transaction_id unique, status)
- messages(id, session_id, sender, message, timestamp)

## Maven on Windows (user install)
If Maven is not installed system-wide:
- Download and extract `apache-maven-3.9.x-bin.zip` to `C:\Users\Asus\maven\`
- Add `C:\Users\Asus\maven\apache-maven-3.9.x\bin` to your user PATH
- Open a new terminal and verify: `mvn -v`

## Database choices
- Default: SQLite (file URL like `jdbc:sqlite:enrollment.db`) uses direct connections (no pool).
- Postgres/MySQL: set `db.url`, `db.username`, `db.password`; HikariCP pooling is enabled automatically for non-SQLite URLs.

## Environment variables (optional)
- Override configuration in `src/main/resources/config.properties` by setting env vars:
	- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (map to `db.url`, etc.)
- Vertex AI (optional assistant features):
	- `PROJECT_ID`, `LOCATION`, `MODEL_NAME` (for UI Assistant)
	- `GEMINI_PROJECT_ID`, `GEMINI_LOCATION` (for `GeminiClient`)
	- If not set, the assistant UI is disabled gracefully.