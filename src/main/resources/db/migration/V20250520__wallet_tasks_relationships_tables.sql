CREATE TABLE "wallet"(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    wallet BIGINT DEFAULT 0,
    user_id BIGINT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE "tasks"(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    description VARCHAR(511),
    status VARCHAR(50),
    cumulative_time_in_seconds BIGINT DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE "relationships"(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,

    UNIQUE (user1_id, user2_id),

    CHECK (user1_id != user2_id),

    FOREIGN KEY (user1_id) REFERENCES user(id),
    FOREIGN KEY (user2_id) REFERENCES user(id)
);

CREATE INDEX idx_user_relationship_1 ON relationships(user1_id);
CREATE INDEX idx_user_relationship_2 ON relationships(user2_id);