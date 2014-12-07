-- You may repeat execute this function to drop all empty tables.

create or replace function dropemptytables()
    returns table(name varchar) as $$
declare
    ar record;
    sql varchar;
begin
    for ar in
        select c.oid, c.relname, n.nspname,
                (select count(*) from pg_constraint s where s.confrelid = c.oid) nfk
            from pg_catalog.pg_class c 
                left join pg_namespace n on c.relnamespace = n.oid
            where relpages = 0 
                and not relisshared and not relhassubclass and relkind = 'r' 
                and n.nspname = 'public'
    loop
        if ar.nfk = 0 then
            return query select cast(ar.relname as varchar) "name";
            sql := 'drop table "' || ar.relname || '"';
            -- return query select cast(sql as varchar);
            execute sql;
        end if;
    end loop;
end
$$ language plpgsql volatile;

-- Examples:
--  select dropemptytables();
