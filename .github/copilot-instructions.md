**Purpose**: Guide Copilot-style AI coding agents to be immediately productive in this repository.

- **Project Type**: Single-module Maven Java 17 desktop Swing application (not Spring Boot despite README).
- **Big Picture**: UI frontend (Swing) -> Service layer (`service/`) -> DAO layer (`dao/`) -> SQLite DB (`enrollment.db`) managed with HikariCP.

**Key Files & Where to Look**
- `Main.java`: App entry; sets up `Theme`, global exception handler, and starts `MobileFrame` (UI main window).
- `MobileFrame.java` and `ui/Screen.java`: Screen routing + animation; `MobileFrame.showScreen()` is the main flow for changing screens.
- `context/ApplicationContext.java`: Manual DI container / singleton holder for DAOs and Services. Prefer using `ApplicationContext` instead of new-ing up DAOs/services directly.
- DAOs: `dao/*` (e.g., `StudentDAO`, `MessageDAO`) each implement `DataAccessObject<T, K>` and use `DatabaseManager` for connections. Look here for SQL and transaction patterns.
- Services: `service/*` (e.g., `EnrollmentService`, `LoginService`) encapsulate business logic and call DAOs. UI screens use these services.
- DB config: `src/main/resources/config.properties` (default `db.url=jdbc:sqlite:enrollment.db`). `enrollment.db` is committed in the repo root.
- AI integration: `util/GeminiClient.java` and `ui/AiAssistantPanel.java` (uses `google-cloud-vertexai` library). These require environment variables to be set; otherwise the assistant is disabled.

**Architecture & Patterns**
- Manual DI via `ApplicationContext`. Add new services/DAOs here to provide a single, globally-consistent instance.
- UI flows are screen-based: create a new `JPanel` in `ui/screens`, add it to `Screen.java`, and update `MobileFrame#createScreen` to inject services when necessary.
- DAO transaction pattern: Each DAO method uses `conn = DatabaseManager.getConnection(); conn.setAutoCommit(false); ... conn.commit(); rollback on exception; finally setAutoCommit(true)`.
- Hex/sensitive facts: Passwords are hashed using BCrypt (see `EnrollmentService`) and checked in `LoginService`.

**Build & Run (Developer Flow)**
- Build jar:
  - `mvn package` (shades a runnable jar per `maven-shade-plugin`).
  - The artifact will be in `target/` (e.g., `demo-0.0.1-SNAPSHOT.jar` or similarly named – verify `target/` list).
- Run locally:
  - `java -jar target/<your-jar>.jar` (ensure the working dir is the repo root so `jdbc:sqlite:enrollment.db` resolves properly).
  - Or run `Main` class from the IDE. The IDE run configuration should set the working directory to the repo root.
- Note: `README.md` mentions Spring Boot and `mvn spring-boot:run` – this is inaccurate. Ignore that unless project rebooted into Spring Boot.

**Database & Fixtures**
- DB is an on-disk SQLite file: `enrollment.db` at repo root. If you want to reset the database, back up or delete `enrollment.db` (no included SQL schema in source).
- Inspect the DB using `sqlite3 enrollment.db` or a GUI DB browser.
- `fixtures/CourseFixtures.java` provides sample course data used in the UI. If you need seed data, either update `enrollment.db` or code a simple SQL seeder.

**AI / Vertex Integration**
- `AiAssistantPanel` and `GeminiClient` use the Vertex AI client – they require environment variables:
  - For `AiAssistantPanel`: `PROJECT_ID`, `LOCATION`, `MODEL_NAME`.
  - For `GeminiClient` (older style): `GEMINI_PROJECT_ID`, `GEMINI_LOCATION`.
- If env vars are not present the UI gracefully disables the assistant; no integration tests exist.

**Conventions & Rules**
- Prefer `ApplicationContext` for instantiating services/DAOs. Avoid scattered `new StudentDAO()` unless for tests or isolated examples.
- Screens are pure `JPanel`s. If state is required, pass services from `ApplicationContext` or use `SessionManager` for shared transient state.
- Persisted IDs: Each DB table uses an `id` int primary key; `student_id` is the human/student facing ID string stored as a separate column.
- Error handling: `GlobalExceptionHandler` displays user-friendly messages and logs stack traces (logging done with SLF4J + Logback).

**Common Development Tasks / Examples**
- Add a new screen called `MyScreen`:
  1. Create file `src/main/java/ui/screens/MyScreen.java` with a `JPanel` subclass.
  2. Add enum entry in `ui/Screen.java`.
  3. If the screen requires a service: modify `MobileFrame#createScreen` to instantiate with `ApplicationContext.get...Service()`.
- Add a new DAO method (pattern):
  - Use `DatabaseManager.getConnection()`, begin transaction (`setAutoCommit(false)`), use `PreparedStatement`, commit, and rollback on exception.
  - Use `LOGGER` to log problems.

**Troubleshooting & Notes**
- If `enrollment.db` is missing, the app will typically create a new file if the schema is set up; this project doesn't include schema migration scripts.
- `pom.xml` includes no test framework or Maven exec plugin. Unit tests are not present (no `src/test` folder).
- Vertex AI usage means local development will work without AI if env vars are not present, but tests/integration will be skipped.

**Where To Modify or Extend**
- UI/UX and screens: `src/main/java/ui/screens/` and `ui/theme/`.
- Backend logic: `src/main/java/service/`.
- Database: `src/main/java/dao/` and `enrollment.db`.
- Configuration: `src/main/resources/config.properties` and environment variables for AI.

**Quick Commands**
- Build: `mvn package`.
- Run locally via JAR: `java -jar target/demo-0.0.1-SNAPSHOT.jar` (or use the jar name produced in `target`).
- Inspect DB: `sqlite3 enrollment.db` or `DB Browser for SQLite`.

Please review and tell me if you'd like this adjusted for a Continuous Integration environment, expanded test guidance, or quick-start/developer scripts added.
