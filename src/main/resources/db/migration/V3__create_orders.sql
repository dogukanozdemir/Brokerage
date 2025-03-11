-- Orders table with customer_id referencing customers(id)
CREATE TABLE orders
(
    id          BIGINT AUTO_INCREMENT,
    customer_id BIGINT         NOT NULL,
    asset_name  VARCHAR(255)   NOT NULL,
    order_side  VARCHAR(10)    NOT NULL, -- Values: 'BUY' or 'SELL'
    size        DECIMAL(18, 2) NOT NULL,
    price       DECIMAL(18, 2) NOT NULL,
    status      VARCHAR(10)    NOT NULL, -- Values: 'PENDING', 'MATCHED', 'CANCELED'
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id)
            ON DELETE CASCADE
);