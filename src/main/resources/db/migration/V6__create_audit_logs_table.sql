CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    changed_by UUID NOT NULL REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    old_value VARCHAR(255) NULL,
    new_value VARCHAR(255) NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_ticket_id ON audit_logs(ticket_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_changed_by ON audit_logs(changed_by);
