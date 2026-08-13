# 🎓 University Student Enrollment System

> **Java & SQLite Student Registration and Course Management Platform.**

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=java)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/SQLite-3-blue?logo=sqlite)](https://www.sqlite.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-red?logo=apachemaven)](https://maven.apache.org/)

**EnrollmentSystem** is an enterprise-grade Java application designed to handle university student enrollments, course assignments, and academic records. Built with a clean Data Access Object (DAO) architecture, SQLite persistence, and Cloud Run / IDX compatibility.

---

## ✨ Features

- 👤 **Student & Course Registration**: Manage student profiles, course catalog entries, and class enrollments.
- 🗄️ **SQLite Persistence (`enrollment.db`)**: Lightweight, portable embedded SQL database layer.
- 🏛️ **Layered Architecture**: Decoupled package design featuring Model, View/UI, Controller, DAO, Service, and Fixtures layers (`src/main/java/`).
- ⚙️ **Configurable Environment**: Centralized configuration properties (`config.properties`) and logging framework (`logback.xml`).
- ☁️ **Cloud & Project IDX Support**: Pre-configured Nix dev environments (`dev.nix`, `project.toml`) and Maven build automation (`pom.xml`).

---

## 🛠 Tech Stack

- **Language**: Java 17+
- **Database**: SQLite 3 (`enrollment.db`)
- **Build Tool**: Apache Maven
- **Logging**: SLF4J / Logback
- **Dev Environment**: VS Code / Project IDX

---

## 🚦 Quick Start

```bash
git clone https://github.com/Renz-cpu-cmd/enrollmentSystem.git
cd enrollmentSystem
mvn clean compile
mvn exec:java -Dexec.mainClass="context.Main"
```
