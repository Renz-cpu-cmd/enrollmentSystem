-- SQLite schema with basic integrity constraints.
-- Apply via your migration tool or sqlite3 before running the app.

CREATE TABLE IF NOT EXISTS students (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    last_name TEXT NOT NULL,
    first_name TEXT NOT NULL,
    middle_name TEXT,
    suffix TEXT,
    birth_date TEXT,
    sex TEXT,
    mobile_number TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    home_address TEXT,
    guardian_name TEXT,
    guardian_mobile TEXT,
    last_school_attended TEXT,
    shs_strand TEXT,
    college TEXT,
    program TEXT,
    year_level INTEGER DEFAULT 1,
    block_section TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    CHECK (length(password) >= 60),
    CHECK (mobile_number LIKE '09_________' OR mobile_number LIKE '+639_________')
);

CREATE INDEX IF NOT EXISTS idx_students_student_id ON students(student_id);
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);
