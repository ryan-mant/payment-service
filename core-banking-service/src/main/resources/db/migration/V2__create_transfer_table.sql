CREATE TABLE transfer (
    id UUID PRIMARY KEY,
    payer_id UUID NOT NULL,
    payee_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transfer_payer FOREIGN KEY (payer_id) REFERENCES wallet(id),
    CONSTRAINT fk_transfer_payee FOREIGN KEY (payee_id) REFERENCES wallet(id)
);

CREATE INDEX idx_transfer_payer ON transfer(payer_id);
CREATE INDEX idx_transfer_payee ON transfer(payee_id);
