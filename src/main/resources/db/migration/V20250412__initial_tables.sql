CREATE TABLE "user"(
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    username VARCHAR(255),
    password VARCHAR(511),
    name VARCHAR(255),
    email VARCHAR(255)
);