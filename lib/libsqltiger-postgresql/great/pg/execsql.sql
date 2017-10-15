--drop function execsql(sql text);

create or replace function execsql(sql text)
    returns text as $$
declare
begin
    execute sql;
    return sql;
end $$ language plpgsql;

