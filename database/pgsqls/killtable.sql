--drop function killtable(schema text, table_name text);

create or replace function killtable(schema text, table_name text)
    returns table(objname text, objtype text, rel text, frel text) as $$
declare
    nsid oid;
    tabid oid;
    qtabname text;
    c record;
    rel text;
    frel text;
    qrel text;
begin
    select oid into nsid from pg_namespace where nspname=schema;
    select oid into tabid from pg_class
        where relnamespace=nsid and relname=table_name;
    qtabname := '"' || schema || '"."' || table_name || '"';
    
    for c in select * from pg_constraint
            where confrelid = tabid
    loop
        select relname::text into rel from pg_class where oid=c.conrelid;
        select relname::text into frel from pg_class where oid=c.confrelid;
        qrel = '"' || schema || '"."' || rel || '"';
        
        execute 'alter table ' || qrel || ' drop constraint ' || c.conname;
        return query select c.conname::text, 'foreign key'::text, rel, frel;
    end loop;
    
    execute 'drop table ' || qtabname;
    return query select qtabname, 'table'::text, null::text, null::text;
end $$ language plpgsql;

-- Example:
-- select * from killtable('old', 'event');

