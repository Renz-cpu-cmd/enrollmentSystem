# OOP Highlights (Inheritance & Polymorphism)

This project uses object-oriented design to keep UI, business logic, and persistence cleanly separated and extensible. Below is a quick, grader‑friendly map of where inheritance and polymorphism show up, with links.

## Inheritance (is‑a relationships)
- Screens as Panels: All screens extend `JPanel` to inherit Swing container behavior and painting.
  - src/main/java/ui/screens/StudentLoginScreen.java
  - src/main/java/ui/screens/DashboardScreen.java
  - src/main/java/ui/screens/PaymentScreen.java
  - src/main/java/ui/screens/AssessmentScreen.java
  - src/main/java/ui/screens/ProgramSelectionScreen.java
  - src/main/java/ui/screens/ReturningBlockScheduleScreen.java
  - src/main/java/ui/screens/DocumentsScreen.java
  - src/main/java/ui/screens/DataPrivacyScreen.java
  - src/main/java/ui/screens/SplashScreen.java
  - src/main/java/ui/screens/BioDataScreen.java
  - src/main/java/ui/screens/BlockSectioningScreen.java
  - src/main/java/ui/screens/CORScreen.java
  - src/main/java/ui/screens/AIAssistantScreen.java

- Custom UI components extend Swing classes to reuse painting/layout and add behavior:
  - src/main/java/ui/theme/CardPanel.java
  - src/main/java/ui/theme/AccordionPanel.java
  - src/main/java/ui/theme/FloatingActionButton.java
  - src/main/java/ui/theme/RoundedButton.java
  - src/main/java/ui/theme/RippleButton.java
  - src/main/java/ui/theme/ProgressStepper.java

## Polymorphism (program to an interface)
- DAO contracts unify persistence operations:
  - Interface usage via `DataAccessObject<T, K>` and repository contracts.
  - Implementations: src/main/java/dao/CourseDAO.java, src/main/java/dao/EnrollmentDAO.java, src/main/java/dao/PaymentDAO.java, src/main/java/dao/StudentDAO.java
  - Services can call different DAO implementations the same way (swap without changing service code).

- Screen contract for navigation:
  - Many screens implement a `ScreenView` contract so the router treats them uniformly.
  - Example implementations listed under “Screens as Panels” above.

- Renderers and handlers through common interfaces:
  - Custom list cell renderer: src/main/java/ui/screens/ReturningBlockScheduleScreen.java (inner `ListCellRenderer`)
  - Global exception handling via `Thread.UncaughtExceptionHandler`: src/main/java/util/GlobalExceptionHandler.java

## Encapsulation & Abstraction
- Service layer encapsulates business rules and orchestration; DAOs hide SQL details.
  - src/main/java/service/EnrollmentService.java
  - src/main/java/service/LoginService.java
- Data access is abstracted behind `DatabaseManager`, which also provides transaction helpers.
  - src/main/java/dao/DatabaseManager.java
- App wiring and shared singletons live in `ApplicationContext` (manual DI), keeping constructors thin.
  - src/main/java/context/ApplicationContext.java

## Why this matters
- Maintainability: UI, business logic, and DB changes are isolated.
- Testability: Services can be tested by swapping DAO implementations.
- Extensibility: New screens or storage backends can plug into existing interfaces without touching callers.

## Quick demo ideas for graders
- Show the navigation treating all `ScreenView` screens the same (switch screens without changing routing code).
- Point out `DataAccessObject<T,K>` implementations and how services don’t care which DAO class is used.
- Open `RoundedButton` or `CardPanel` to show UI inheritance and overridden painting methods.
