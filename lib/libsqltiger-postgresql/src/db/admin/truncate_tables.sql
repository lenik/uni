--drop function truncate_tables(p_user in varchar, p_schema_name in varchar);

create or replace function truncate_tables(
        p_user in varchar default null,
        p_schema_name in varchar default 'public'
    ) returns table(schemaname text, tableowner text, tablename text) as $$
declare
    statements cursor for
        select * from pg_tables a
        where (p_user is null or a.tableowner = p_user)
          and a.schemaname = p_schema_name;
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

