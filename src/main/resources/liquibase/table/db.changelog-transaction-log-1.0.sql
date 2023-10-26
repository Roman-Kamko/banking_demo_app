--liquibase formatted sql

--changeset RomanKamko:1
CREATE TABLE IF NOT EXISTS transaction_log
(
    id         BIGSERIAL PRIMARY KEY,
    operation  VARCHAR(16),
    amount     DECIMAL(11, 2),
    account_id BIGINT REFERENCES account(id),
    date_time  TIMESTAMP
);
