--drop function switch_triggers("user" in varchar, "schema" in varchar);

create or replace function switch_triggers(
        enable in boolean,
        "user" in varchar default null,
        "schema" in varchar default 'public'
    ) returns table(schemaname text, tableowner text, tablename text) as $$
declare
    statements cursor for
        select * from pg_tables a
        where ("user" is null or a.tableowner = "user")
          and a.schemaname = "schema";
begin
    for stmt in statements loop
        if enable then
            execute 'alter table ' || quote_ident(stmt.tablename) || ' enable trigger all; ';
        else
            execute 'alter table ' || quote_ident(stmt.tablename) || ' disable trigger all; ';
        end if;
        return query select
            stmt.schemaname::text,
            stmt.tableowner::text, 
            stmt.tablename::text;
    end loop;
end $$ language plpgsql;

-- Example:
-- select * from switch_triggers(false);
-- (restore DDL...)
-- select * from switch_triggers(true);
