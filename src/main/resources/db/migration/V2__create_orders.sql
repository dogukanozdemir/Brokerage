CREATE TABLE orders
(
    id          BIGINT AUTO_INCREMENT,
    customer_id VARCHAR(255)   NOT NULL,
    asset_name  VARCHAR(255)   NOT NULL,
    order_side  VARCHAR(10)    NOT NULL, -- Values: 'BUY' or 'SELL'
    size        DECIMAL(18, 2) NOT NULL,
    price       DECIMAL(18, 2) NOT NULL,
    status      VARCHAR(10)    NOT NULL, -- Values: 'PENDING', 'MATCHED', 'CANCELED'
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
