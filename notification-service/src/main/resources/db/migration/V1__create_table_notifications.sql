CREATE TABLE notifications
(
    id             UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    transaction_id UUID         NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL,
    message        TEXT         NOT NULL,
    status         VARCHAR(50)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP
);

CREATE INDEX idx_notification_email ON notifications (email);