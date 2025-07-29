CREATE TABLE "purchasing_history" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    price FLOAT DEFAULT 0,
    description VARCHAR(511),
    purchase_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);