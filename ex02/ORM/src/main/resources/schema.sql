DROP TABLE IF EXISTS simple_user CASCADE;

CREATE TABLE IF NOT EXISTS simple_user (
	id INTEGER IDENTITY PRIMARY KEY,
	first_name VARCHAR(10) NOT NULL,
	last_name VARCHAR(10) NOT NULL,
	age INT,
	is_admin BOOLEAN,
	rating FLOAT,
	operations INTEGER
);