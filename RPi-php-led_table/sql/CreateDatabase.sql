CREATE DATABASE IF NOT EXISTS led_table;

grant usage on *.* to user@localhost identified by 'user';

grant all privileges on led_table.* to user@localhost;

USE led_table

CREATE TABLE IF NOT EXISTS arduino_mode (
	mode_id  int NOT NULL AUTO_INCREMENT,
	mode_num int NOT NULL,
	mode_desc varchar(255),
	mode_seq int NOT NULL,
	PRIMARY KEY (mode_id),
	UNIQUE (mode_num),
	UNIQUE (mode_seq)
);

CREATE TABLE IF NOT EXISTS selection (
	selection_id    int NOT NULL AUTO_INCREMENT,
	selection_mode  int NOT NULL,
	selection_date  datetime NOT NULL,
	selection_parm1 varchar(512),
	selection_parm2 varchar(255),
	selection_parm3 varchar(255),
	selection_parm4 varchar(255),
	selection_parm5 blob(288),
	PRIMARY KEY (selection_id)
);
