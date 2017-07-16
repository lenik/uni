-- drop function monthtrends(text, text, text, text, text);

create or replace function monthtrends(
        table_name text,
        field_g text,
        field_1 text default '0',
        field_2 text default '0',
        table_schema text default 'public')
    returns table(
        "year" int, 
        "month" int,
        "count" bigint,
        sum_1 real,
        sum_2 real,
        t0 timestamptz, 
        t1 timestamptz) as $$
declare
    q_table varchar;
    v record;
    sql text;
begin
    q_table := '"' || table_schema || '"."' || table_name || '"';
    sql := 'select '
            || 'extract(year from ' || field_g || ')::int "year", '
            || 'extract(month from ' || field_g || ')::int "month", '
            || 'count(*)::bigint, '
            || 'sum(' || field_1 || ')::real "sum_1", '
            || 'sum(' || field_2 || ')::real "sum_2", '
            || 'min(' || field_g || ') "t0", '
            || 'max(' || field_g || ') "t1"'
        || ' from ' || q_table
        || ' group by '
            || 'extract(year from ' || field_g || ')::int, '
            || 'extract(month from ' || field_g || ')::int';
    return query execute sql;
end
$$ language plpgsql;

-- Examples:
--  select * from monthtrends('person', 'lastmod');
--  select * from monthtrends('person', 'lastmod', field_1 := 'id');
