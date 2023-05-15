-- liquibase formatted sql

-- changeset fill tables:add data

INSERT INTO roles ("name")
VALUES ('ADMIN'),
		('JOURNALIST'),
		('SUBSCRIBER');


INSERT INTO users (first_name, last_name, email, password, role_id)
VALUES
	('Nick', 'Johnson', 'johnson@gmail.us', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'ADMIN')),
	('Mike', 'Scholz', 'scholz@yandex.ru', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Joseph', 'Black', 'black@yandex.ru', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Karl', 'Bit', 'karl@yandex.ru', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Philip', 'Bosch', 'philbosch@yandex.ru', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Linda', 'Pink', 'linda@yandex.ru', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Yuri', 'Ivanov', 'ivanov@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Eva', 'Simonova', 'simonova@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Peter', 'Mask', 'peter@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Kristina', 'Marsimova', 'kris@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Alena', 'Alenova', 'alena@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Viktor', 'Viktorov', 'viktorov@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Alex', 'Biden', 'alex@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Tony', 'Mag', 'tony@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Tom', 'Tomov', 'tomov@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Valeriy', 'Fedorov', 'fedorov@gmail.com', '$2a$10$O.F1CwjVoKO.CsY5p1laee2V5vgsVOPqPlDyxjg1bF9epsIFqWD0u', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER'));
