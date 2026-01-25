CREATE TABLE wallet (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    "version" BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_wallet_cpf_cnpj ON wallet(cpf_cnpj);
CREATE INDEX idx_wallet_email ON wallet(email);
