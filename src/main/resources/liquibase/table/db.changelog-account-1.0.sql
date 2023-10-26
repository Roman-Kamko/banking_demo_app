--liquibase formatted sql

--changeset RomanKamko:1
CREATE TABLE IF NOT EXISTS account
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(32),
    pin        VARCHAR(255),
    balance    DECIMAL(11, 2),
    created_at TIMESTAMP
);
