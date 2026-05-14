CREATE TABLE IF NOT EXISTS tickets (
    id UUID PRIMARY KEY,
    ticket_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    issue_title VARCHAR(300) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE INDEX IF NOT EXISTS idx_tickets_email ON tickets(email);
CREATE INDEX IF NOT EXISTS idx_tickets_ticket_number ON tickets(ticket_number);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status);
