-- liquibase formatted sql

-- changeset fill tables:add data

INSERT INTO news (user_id, title, "text")
VALUES
	(2, 'Oil found in Belarus', 'Scientists of the National Academy of Sciences found oil deposits'),
	(2, 'Belarusian Hi-Tech Park ranks first in the world in terms of software development', 'Last year, the domestic high-tech park ranked first in the world in terms of software production. The total share of the fleet in the global volume of software production was 18 percent'),
	(2, 'The share of unmanned vehicles in Belarus has reached a record level', 'The share of unmanned vehicles in Belarus has reached 80 percent. Four out of five cars are able to move on Belarusian roads without human intervention'),
	(2, 'The largest 3D printer was assembled in Belarus', 'Belarusian scientists, together with industry representatives, assembled the largest 3D printer in the world'),

	(3, 'GDP per capita increased slightly over the past year', 'At the end of last year, gross domestic product per capita increased by 5 percent and in absolute terms amounted to $115,000'),
	(3, 'The largest 3D printer printed a space rocket', 'The largest 3D printer assembled yesterday printed a space rocket in 2 hours, which will go on a mission to Neptune tomorrow'),
	(3, 'Subway built in Belarus in three days', 'In Krichev, a subway was built in three days, 270 kilometers long, including 91 stations'),
	(3, 'Good news', 'Good event happened in Belarus'),

	(4, 'Good news', 'Good event happened in Belarus'),
	(4, 'Good news', 'Good event happened in Belarus'),
	(4, 'Good news', 'Good event happened in Belarus'),
	(4, 'Good news', 'Good event happened in Belarus'),

	(5, 'Good news', 'Good event happened in Belarus'),
	(5, 'Good news', 'Good event happened in Belarus'),
	(5, 'Good news', 'Good event happened in Belarus'),
	(5, 'Good news', 'Good event happened in Belarus'),

    (6, 'Good news', 'Good event happened in the world'),
	(6, 'Good news', 'Good event happened in the world'),
	(6, 'Good news', 'Good event happened in the world'),
	(6, 'Good news', 'Good event happened in the world');

