-- liquibase formatted sql

-- changeset create tables:add constraints

CREATE TABLE IF NOT EXISTS roles (
	role_id SERIAL PRIMARY KEY,
	"name" VARCHAR (20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
	id BIGSERIAL PRIMARY KEY,
	first_name VARCHAR (30),
	last_name VARCHAR (30),
	email VARCHAR (50) UNIQUE NOT NULL,
	password VARCHAR NOT NULL,
	role_id INTEGER NOT NULL REFERENCES roles
);