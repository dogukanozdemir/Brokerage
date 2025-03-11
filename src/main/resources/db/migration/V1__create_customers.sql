CREATE TABLE customers
(
    id          BIGINT AUTO_INCREMENT,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL DEFAULT 'USER',
    create_date TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
