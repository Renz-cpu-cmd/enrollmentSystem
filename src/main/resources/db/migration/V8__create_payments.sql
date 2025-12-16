-- Payments linked to assessments
DROP TABLE IF EXISTS payments;

CREATE TABLE payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    assessment_id INTEGER NOT NULL,
    amount NUMERIC NOT NULL,
    payment_date TEXT,
    payment_method TEXT,
    reference_no TEXT,
    created_at TEXT,
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payments_assessment_id ON payments(assessment_id);
