--\import timetab.sector
--\import timetab.timetable

    create sequence timeslot_seq start with 1000;
    create table "timeslot"(
        id          int primary key default nextval('timeslot_seq'),
--\mixin lily.mixin.LabelExVer

        parent      bigint not null references timetable on update cascade on delete cascade,
        seq         int not null,
        start       time not null,
        duration    int not null,
        "end"         time not null,
        type        varchar(30) not null,
        sector      bigint references sector on update cascade on delete set null
    );

    create index timeslot_label         on "timeslot"(label);
    create index timeslot_lastmod       on "timeslot"(lastmod desc);

    comment on table "timeslot" is 'Timetable Slot';
    comment on column "timeslot".seq is 'Slot sequence number.';
    comment on column "timeslot".duration is 'Slot duration in minutes.';
    comment on column "timeslot".start is 'Slot start time';
    comment on column "timeslot"."end" is 'Slot end time';
    comment on column "timeslot".type is 'Type string, A* means available';

    insert into "timeslot"
        (id, parent,seq,start, duration,"end",      type,           description) values
        (1,  1,     1,  '06:35',    25, '07:00',    'A0-L',         ''),
        (2,  1,     2,  '07:00',    60, '08:00',    'Reserved',     'Prepare'),
        (3,  1,     3,  '08:00',    25, '08:25',    'FEM',          ''),
        (4,  1,     4,  '08:25',    15, '08:40',    'Traffic',      'Ride to Station'),
        (5,  1,     5,  '08:40',    45, '09:25',    'A1-T',         '@Train'),
        (6,  1,     6,  '09:25',    15, '09:40',    'Traffic',      'Walk to work'),
        (7,  1,     7,  '09:40',    10, '09:50',    'Reserved',     'Prepare'),
        (8,  1,     8,  '09:50',    100,'11:30',    'A2-Maj',       'Morning major'),
        (9,  1,     9,  '11:30',    50, '12:20',    'Reserved',     'Lunch'),
        (10, 1,     10, '12:20',    10, '12:30',    'Reserved',     'Prepare'),
        (11, 1,     11, '12:30',    30, '13:00',    'A3-N',         ''),
        (12, 1,     12, '13:00',    60, '14:00',    'Break/Sleep',  'Nap'),
        (13, 1,     13, '14:00',    90, '15:30',    'A4-Major',     'Afternoon major'),
        (14, 1,     14, '15:30',    30, '16:00',    'Break',        'Afternoon Tea'),
        (15, 1,     15, '16:00',    90, '17:30',    'A5-Minor',     'Afternoon minor'),
        (16, 1,     16, '17:30',    15, '17:45',    'Break',        'Supper Snack'),
        (17, 1,     17, '17:45',    90, '19:15',    'A6-Major',     'Evening major'),
        (18, 1,     18, '19:15',    15, '19:30',    'Traffic',      'Ride to yoga'),
        (19, 1,     19, '19:30',    60, '20:30',    'FEM',          'Yoga'),
        (20, 1,     20, '20:30',    15, '20:45',    'Traffic',      'Ride to work'),
        (21, 1,     21, '20:45',    45, '21:30',    'A7-Minor',     'Evening minor'),
        (22, 1,     22, '21:30',    15, '21:45',    'Traffic',      'Walk to Wulin'),
        (23, 1,     23, '21:45',    45, '22:30',    'A8-T',         '@Train'),
        (24, 1,     24, '22:30',    15, '22:45',    'Traffic',      'Ride to LP'),
        (25, 1,     25, '22:45',    15, '23:00',    'Reserved',     'Get Parcels'),
        (26, 1,     26, '23:00',    40, '23:40',    'A9-Final',     ''),
        (27, 1,     27, '23:40',    20, '00:00',    'FEM',          ''),
        (28, 1,     28, '00:00',    10, '00:10',    'Traffic',      'Ride to home'),
        (29, 1,     29, '00:10',    25, '00:35',    'Reserved',     '"Clean up, tide up, memories"'),
        (30, 1,     30, '00:35',    360,'06:35',    'Break/Sleep',  'Main sleep');
