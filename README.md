# Enrollment Desktop App (Swing)

Java 17 Swing desktop app using SQLite (HikariCP). Not Spring Boot.

## Build & Run
- Build shaded jar: `mvn package`
- Run from repo root: `java -jar target/demo-0.0.1-SNAPSHOT-shaded.jar`

## Database migrations (Flyway)
- Migrations live in `src/main/resources/db/migration` (V1 students, V2 courses/enrollments/payments, V3 messages + seed courses, V4 student lookup indexes).
- App auto-runs Flyway at startup using `db.url` from `config.properties` or environment.
- Manual/CI run example (SQLite):
	- `mvn -Dflyway.url=jdbc:sqlite:enrollment.db -Dflyway.driver=org.sqlite.JDBC -Dflyway.locations=filesystem:src/main/resources/db/migration flyway:migrate`
	- Override `flyway.url` in CI to point at your deployment database.

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