-- Messaging table and seed data for courses.

CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id TEXT NOT NULL,
    sender TEXT NOT NULL,
    message TEXT NOT NULL,
    timestamp TEXT DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_messages_session ON messages(session_id);

-- Seed course catalog (idempotent for SQLite via INSERT OR IGNORE)
INSERT OR IGNORE INTO courses(code, name, description, units) VALUES
    ('CS101', 'Introduction to Programming', 'Fundamentals of programming with Java', 3),
    ('CS102', 'Data Structures', 'Arrays, lists, trees, and graphs', 4),
    ('CS201', 'Databases', 'Relational modeling and SQL with SQLite', 3),
    ('CS202', 'Operating Systems', 'Processes, threads, and synchronization', 3),
    ('CS301', 'Software Engineering', 'Requirements, design patterns, and testing', 3);
