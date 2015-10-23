-- drop function tablestat();

create or replace function tablestat()
    returns table(table_schema text, table_name text, table_type text, rows bigint) as $$
declare
    qtabname varchar;
    v record;
    sql text;
begin
    for v in select * from information_schema.tables
    loop
        qtabname := '"' || v.table_schema || '"."' || v.table_name || '"';
        sql := 'select count(*) from ' || qtabname;
        execute sql into rows;
        return query select
                v.table_schema::text,
                v.table_name::text,
                v.table_type::text,
                rows;
    end loop;
end
$$ language plpgsql;

-- Examples:
--  select * from tablestat();

-- WARNING:
--  Don't create view for tablestat(), which will cause dead loop.

