CREATE TABLE "wallet" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    balance BIGINT DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE "tasks"(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    taskDescription VARCHAR(511),
    status VARCHAR(511),
    time_in_seconds BIGINT DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE "relationships"(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    partner_id BIGINT NOT NULL,
    status VARCHAR(511),

    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (partner_id) REFERENCES "user"(id)
);