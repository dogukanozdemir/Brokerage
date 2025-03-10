CREATE TABLE assets
(
    customer_id VARCHAR(255)   NOT NULL,
    asset_name  VARCHAR(255)   NOT NULL,
    size        DECIMAL(18, 2) NOT NULL,
    usable_size DECIMAL(18, 2) NOT NULL,
    PRIMARY KEY (customer_id, asset_name)
);
