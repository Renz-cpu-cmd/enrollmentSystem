-- Rebuild students table from legacy schema (contact_number/address/course fields) to new schema with password/mobile_number.
ALTER TABLE students RENAME TO students_old;

CREATE TABLE students (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	student_id TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL,
	last_name TEXT NOT NULL,
	first_name TEXT NOT NULL,
	middle_name TEXT,
	suffix TEXT,
	birth_date TEXT,
	sex TEXT,
	mobile_number TEXT,
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
	CHECK (mobile_number IS NULL OR mobile_number LIKE '09_________' OR mobile_number LIKE '+639_________')
);

INSERT INTO students (
	id, student_id, password, last_name, first_name, middle_name, suffix, birth_date, sex,
	mobile_number, email, home_address, guardian_name, guardian_mobile, last_school_attended,
	shs_strand, college, program, year_level, block_section, created_at
)
SELECT
	id,
	student_id,
	'012345678901234567890123456789012345678901234567890123456789' as password,
	last_name,
	first_name,
	NULL as middle_name,
	NULL as suffix,
	NULL as birth_date,
	gender as sex,
	contact_number as mobile_number,
	email,
	address as home_address,
	NULL as guardian_name,
	NULL as guardian_mobile,
	NULL as last_school_attended,
	NULL as shs_strand,
	NULL as college,
	course as program,
	year_level,
	block as block_section,
	created_at
FROM students_old;

DROP TABLE students_old;

CREATE INDEX IF NOT EXISTS idx_students_student_id ON students(student_id);
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);
CREATE INDEX IF NOT EXISTS idx_students_mobile_number ON students(mobile_number);
CREATE INDEX IF NOT EXISTS idx_students_last_name ON students(last_name);
