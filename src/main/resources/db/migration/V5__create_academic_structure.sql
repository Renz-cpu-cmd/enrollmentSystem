-- Academic structure for blocks, sections, schedules, and assessments.
-- Note: Numbered as V5 to avoid clashing with existing V1-V4 migrations.

CREATE TABLE IF NOT EXISTS sections (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    program TEXT,
    year_level INTEGER NOT NULL DEFAULT 1,
    shift TEXT,
    capacity INTEGER NOT NULL DEFAULT 40,
    created_at TEXT DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_sections_program_year ON sections(program, year_level);
CREATE INDEX IF NOT EXISTS idx_sections_shift ON sections(shift);

CREATE TABLE IF NOT EXISTS blocks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    section_id INTEGER NOT NULL,
    block_code TEXT NOT NULL UNIQUE,
    title TEXT,
    description TEXT,
    capacity INTEGER NOT NULL DEFAULT 40,
    active INTEGER NOT NULL DEFAULT 1,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_blocks_section ON blocks(section_id);
CREATE INDEX IF NOT EXISTS idx_blocks_active ON blocks(active);

CREATE TABLE IF NOT EXISTS schedules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    block_id INTEGER NOT NULL,
    course_id INTEGER,
    course_code TEXT NOT NULL,
    subject TEXT NOT NULL,
    day_pattern TEXT NOT NULL,
    time_start TEXT NOT NULL,
    time_end TEXT NOT NULL,
    room TEXT,
    instructor TEXT,
    units REAL NOT NULL DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (block_id) REFERENCES blocks(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_schedules_block ON schedules(block_id);
CREATE INDEX IF NOT EXISTS idx_schedules_course ON schedules(course_id);
CREATE INDEX IF NOT EXISTS idx_schedules_day_time ON schedules(day_pattern, time_start, time_end);

-- Assessment and fee breakdown per enrollment
CREATE TABLE IF NOT EXISTS assessments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    enrollment_id INTEGER NOT NULL UNIQUE,
    total_units REAL NOT NULL DEFAULT 0,
    tuition_fee NUMERIC NOT NULL DEFAULT 0,
    misc_fee NUMERIC NOT NULL DEFAULT 0,
    lab_fee NUMERIC NOT NULL DEFAULT 0,
    other_fee NUMERIC NOT NULL DEFAULT 0,
    total_due NUMERIC NOT NULL DEFAULT 0,
    currency TEXT NOT NULL DEFAULT 'PHP',
    status TEXT NOT NULL DEFAULT 'PENDING',
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_assessments_status ON assessments(status);

CREATE TABLE IF NOT EXISTS assessment_fees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    assessment_id INTEGER NOT NULL,
    fee_code TEXT,
    description TEXT NOT NULL,
    amount NUMERIC NOT NULL DEFAULT 0,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_assessment_fees_assessment ON assessment_fees(assessment_id);
CREATE INDEX IF NOT EXISTS idx_assessment_fees_sort ON assessment_fees(assessment_id, sort_order);
