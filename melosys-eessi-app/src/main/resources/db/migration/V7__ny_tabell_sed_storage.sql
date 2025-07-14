CREATE TABLE sed_storage
(
    id              SERIAL PRIMARY KEY,
    sed_id          VARCHAR(50) NOT NULL,
    sed             JSONB       NOT NULL,
    storage_reason  VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
