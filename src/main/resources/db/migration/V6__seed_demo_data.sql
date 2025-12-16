-- ==========================================
-- PART 1: ACADEMIC STRUCTURE
-- ==========================================

-- Section: BSIT-2A Morning
INSERT OR IGNORE INTO sections (code, name, program, year_level, shift, capacity)
VALUES ('BSIT-2A', 'BSIT-2A', 'BSIT', 2, 'Morning', 40);

-- Block: Block A linked to BSIT-2A
INSERT OR IGNORE INTO blocks (section_id, block_code, title, description, capacity, active)
SELECT id, 'Block A', 'Block A - Morning', 'BSIT-2A Morning Cohort', 40, 1
FROM sections WHERE code = 'BSIT-2A' LIMIT 1;

-- Helper CTE for block id to insert Schedules
WITH target_block AS (
    SELECT b.id AS block_id FROM blocks b WHERE b.block_code = 'Block A' LIMIT 1
)
INSERT OR IGNORE INTO schedules (block_id, course_id, course_code, subject, day_pattern, time_start, time_end, room, instructor, units)
SELECT block_id, NULL, 'CS201', 'Data Structures & Algorithms', 'Mon/Wed', '08:00', '09:30', 'CL-201', 'Prof. Serrano', 3.0 FROM target_block UNION ALL
SELECT block_id, NULL, 'CS202', 'Object-Oriented Programming', 'Mon/Wed', '09:45', '11:15', 'CL-202', 'Engr. De Vera', 3.0 FROM target_block UNION ALL
SELECT block_id, NULL, 'CS203', 'Database Systems', 'Tue/Thu', '08:00', '09:30', 'CL-101', 'Prof. Santos', 3.0 FROM target_block UNION ALL
SELECT block_id, NULL, 'CS204', 'Computer Networks', 'Tue/Thu', '09:45', '11:15', 'NET-LAB', 'Engr. Ramos', 3.0 FROM target_block UNION ALL
SELECT block_id, NULL, 'GEC205', 'Ethics and the Modern World', 'Fri', '08:00', '10:00', 'NB-204', 'Ms. Flores', 2.0 FROM target_block UNION ALL
SELECT block_id, NULL, 'PE202', 'Physical Education 2', 'Fri', '10:15', '11:15', 'Gym', 'Coach Cruz', 2.0 FROM target_block;

-- ==========================================
-- PART 2: TEST STUDENTS
-- ==========================================

INSERT OR IGNORE INTO students 
(student_id, last_name, first_name, password, student_type, year_level, program, sex, email, home_address) 
VALUES 
('2023-0001', 'Penduko', 'Pedro', '$2a$10$z.4i3aEERzzrRUDWzP9g6Oe2TY4uSeOHHlpyA0WD1Ykd4IkHg16E2', 'REGULAR', 2, 'BSIT', 'Male', 'pedro@test.com', '123 Test St');

INSERT OR IGNORE INTO students 
(student_id, last_name, first_name, password, student_type, year_level, program, sex, email, home_address) 
VALUES 
('2021-0001', 'Clara', 'Maria', '$2a$10$z.4i3aEERzzrRUDWzP9g6Oe2TY4uSeOHHlpyA0WD1Ykd4IkHg16E2', 'REGULAR', 4, 'BSCS', 'Female', 'maria@test.com', '234 Sample Ave');

INSERT OR IGNORE INTO students 
(student_id, last_name, first_name, password, student_type, year_level, program, sex, email, home_address) 
VALUES 
('2022-0050', 'Tamad', 'Juan', '$2a$10$z.4i3aEERzzrRUDWzP9g6Oe2TY4uSeOHHlpyA0WD1Ykd4IkHg16E2', 'IRREGULAR', 3, 'BSIT', 'Male', 'juan@test.com', '345 Demo Blvd');

INSERT OR IGNORE INTO students 
(student_id, last_name, first_name, password, student_type, year_level, program, sex, email, home_address) 
VALUES 
('2024-0010', 'Cruz', 'Ana', '$2a$10$z.4i3aEERzzrRUDWzP9g6Oe2TY4uSeOHHlpyA0WD1Ykd4IkHg16E2', 'REGULAR', 1, 'BSN', 'Female', 'ana@test.com', '456 Example Rd');