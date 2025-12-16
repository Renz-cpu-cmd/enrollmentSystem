-- V7: Rebuild enrollments table to ensure block_id exists
-- We remove PRAGMA commands to keep Flyway happy.

DROP TABLE IF EXISTS enrollments;

CREATE TABLE enrollments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    block_id INTEGER, -- Nullable to support irregular students later
    term TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING', -- ENROLLED, DROPPED, PENDING
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (block_id) REFERENCES blocks(id) ON DELETE SET NULL
);

-- Indices for performance
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_block ON enrollments(block_id);