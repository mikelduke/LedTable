insert into arduino_mode (mode_num, mode_desc, mode_seq) values (0, 'Off', 10);
insert into arduino_mode (mode_num, mode_desc, mode_seq) values (1, 'Demo', 20);
insert into arduino_mode (mode_num, mode_desc, mode_seq) values (2, 'Pulse1', 30);
insert into arduino_mode (mode_num, mode_desc, mode_seq) values (3, 'Pulse2', 40);
insert into arduino_mode (mode_num, mode_desc, mode_seq) values (4, 'Array Mode', 50);
insert into arduino_mode (mode_num, mode_desc, mode_seq) values (5, 'Set Color', 60);
insert into arduino_mode (mode_num, mode_desc, mode_seq) values (6, 'Pulse - Random', 70);

insert into selection (selection_mode, selection_date) values (0, now());
insert into selection (selection_mode, selection_date) values (6, now());

select selection_mode, selection_date from selection order by selection_date desc limit 1;

--insert into selection (selection_mode, selection_date, selection_parm1) values (7, now(), '/home/mikel/mdp3/rpi/java/LedTable/test.bmp');
--insert into selection (selection_mode, selection_date, selection_parm1, selection_parm2) values (8, now(), '/home/mikel/mdp3/rpi/java/LedTable/testAnimation2', 1000);
