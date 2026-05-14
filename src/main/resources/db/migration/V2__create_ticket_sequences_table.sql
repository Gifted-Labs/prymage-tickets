CREATE TABLE IF NOT EXISTS ticket_sequences (
    year INT PRIMARY KEY,
    last_sequence INT NOT NULL DEFAULT 0
);
