-- V4: Create indexes for faster student lookups
-- Fixed: Changed 'contact_number' to 'mobile_number' to match V1

CREATE INDEX IF NOT EXISTS idx_students_last_name ON students(last_name);
CREATE INDEX IF NOT EXISTS idx_students_program ON students(program);
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);

-- This was the line causing the error:
CREATE INDEX IF NOT EXISTS idx_students_mobile ON students(mobile_number);