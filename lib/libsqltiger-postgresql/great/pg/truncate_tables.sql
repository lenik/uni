--drop function truncate_tables("user" in varchar, "schema" in varchar);

create or replace function truncate_tables(
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
        execute 'truncate table ' || quote_ident(stmt.tablename) || ' cascade; ';
        return query select
            stmt.schemaname::text,
            stmt.tableowner::text, 
            stmt.tablename::text;
    end loop;
end $$ language plpgsql;

-- Example:
-- select * from truncate_tables();

