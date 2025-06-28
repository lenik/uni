--\import lily.account.user

    create sequence timetable_seq start with 1000;
    create table "timetable"(
        id          bigint primary key default nextval('timetable_seq'),
--\mixin lily.mixin.Acl_rw-r--r--
--\mixin lily.mixin.LabelExVer

        dummy       int
    );

    create index timetable_label         on "timetable"(label);
    create index timetable_lastmod       on "timetable"(lastmod desc);

    comment on table "timetable" is 'Time Table';

    insert into "timetable"
        (id, label, description,        uid, mode) values
        (1, 'demo', 'Demo Time Table',  0, 777)
        ;
        
