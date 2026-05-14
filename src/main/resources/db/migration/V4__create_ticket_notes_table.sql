CREATE TABLE IF NOT EXISTS ticket_notes (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id),
    note_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ticket_notes_ticket_id ON ticket_notes(ticket_id);
CREATE INDEX IF NOT EXISTS idx_ticket_notes_note_type ON ticket_notes(note_type);
