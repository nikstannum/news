INSERT INTO roles ("name")
VALUES ('ADMIN'),
		('JOURNALIST'),
		('SUBSCRIBER');


INSERT INTO users (first_name, last_name, email, password, role_id)
VALUES
	('Nick', 'Johnson', 'johnson@gmail.us', 'qwerty', (SELECT r.role_id FROM roles r WHERE r."name" = 'ADMIN')),
	('Mike', 'Scholz', 'scholz@yandex.ru', 'password', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Joseph', 'Black', 'black@yandex.ru', 'hardpassword', (SELECT r.role_id FROM roles r WHERE r."name" = 'JOURNALIST')),
	('Yuri', 'Ivanov', 'ivanov@gmail.com', 'zxcvb', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Peter', 'Mask', 'peter@gmail.com', 'vcxzb', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER')),
	('Valeriy', 'Fedorov', 'fedorov@gmail.com', 'fedorov123', (SELECT r.role_id FROM roles r WHERE r."name" = 'SUBSCRIBER'));
