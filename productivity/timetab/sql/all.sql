set search_path = public;
--\import great.pg

create schema lily;
set search_path = lily, public;
--\import lily.account.user

create schema timetab;
set search_path = timetab, lily, public;
--\import timetab

do $$ begin
    execute 'alter database '
        || current_database()
        ||' set search_path to timetab, lily, public';
end $$;
