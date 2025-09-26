CREATE TABLE saga_transaction (
     saga_id VARCHAR(255) PRIMARY KEY,
     order_id VARCHAR(255) NOT NULL,
     state VARCHAR(50) NOT NULL,
     current_step VARCHAR(50),
     created_at TIMESTAMP NOT NULL,
     updated_at TIMESTAMP NOT NULL,
     error_message TEXT
 );

 CREATE INDEX idx_saga_order_id ON saga_transaction(order_id);
 CREATE INDEX idx_saga_state ON saga_transaction(state);