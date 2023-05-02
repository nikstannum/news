CREATE TABLE IF NOT EXISTS comments (
	id BIGSERIAL PRIMARY KEY,
	news_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	text TEXT,
	create_time TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc')
);