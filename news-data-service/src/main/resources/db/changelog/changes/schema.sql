-- liquibase formatted sql

-- changeset create tables:add constraints

CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL,
	title VARCHAR (150) NOT NULL,
	"text" TEXT,
	create_time TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc')
);

CREATE TABLE IF NOT EXISTS comments (
	id BIGSERIAL PRIMARY KEY,
	news_id BIGINT  REFERENCES news,
	user_id BIGINT NOT NULL,
	text TEXT,
	create_time TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc')
);