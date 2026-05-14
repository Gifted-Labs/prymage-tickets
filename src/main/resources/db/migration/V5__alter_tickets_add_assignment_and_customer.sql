ALTER TABLE tickets
    ADD COLUMN IF NOT EXISTS assigned_to UUID NULL REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS customer_user_id UUID NULL REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_tickets_assigned_to ON tickets(assigned_to);
CREATE INDEX IF NOT EXISTS idx_tickets_customer_user_id ON tickets(customer_user_id);
