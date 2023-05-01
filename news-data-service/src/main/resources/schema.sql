CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL,
	title VARCHAR (150) NOT NULL,
	"text" TEXT,
	create_time TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc')
);