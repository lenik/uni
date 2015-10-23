--drop function purgeschema(schema text);

create or replace function purgeschema(schema text)
    returns table(nspname text, table_name text, obj_name text, obj_type text) as $$
declare
    v record;
    qname text;
begin
    for v in select conname, contype, o.relname, n.nspname, n.nspowner
            from pg_constraint c
                left join pg_class o on c.conrelid=o.oid
                left join pg_namespace n on o.relnamespace=n.oid
            where c.contype = 'f' -- foreign key constraint
                and n.nspname = schema
    loop
        qname = '"' || v.nspname || '"."' || v.relname || '"';
        
        execute 'alter table ' || qname || ' drop constraint "' || v.conname || '"';
        
        return query select
            v.nspname::text,
            v.relname::text,
            v.conname::text,
            'fk'::text;
    end loop;
    
    for v in select o.relname, o.relkind, n.nspname, n.nspowner
            from pg_class o
                left join pg_namespace n on o.relnamespace=n.oid
            where (o.relkind = 'r' or o.relkind = 'v')
                and n.nspname = schema
            order by o.relkind desc -- so 'v' comes first, then 'r'.
    loop
        qname = '"' || v.nspname || '"."' || v.relname || '"';
        
        if v.relkind = 'v' then
            execute 'drop view ' || qname;
        else
            execute 'drop table ' || qname;
        end if;
        
        return query select
            v.nspname::text,
            v.relname::text,
            v.relname::text,
            v.relkind::text;
    end loop;
    
    for v in select t.typname, n.nspname, n.nspowner
            from pg_type t
                left join pg_namespace n on t.typnamespace=n.oid
            where typtype='e'
                and n.nspname = schema
    loop
        qname = '"' || v.nspname || '"."' || v.typname || '"';
        
        execute 'drop type ' || qname;
        
        return query select
            v.nspname::text,
            null::text,
            v.typname::text,
            'enum'::text;
    end loop;
    
    for v in select o.relname, o.relkind, n.nspname, n.nspowner
            from pg_class o
                left join pg_namespace n on o.relnamespace=n.oid
            where o.relkind = 'S'
                and n.nspname = schema
    loop
        qname = '"' || v.nspname || '"."' || v.relname || '"';
        
        execute 'drop sequence ' || qname;
        
        return query select
            v.nspname::text,
            null::text,
            v.relname::text,
            'seq'::text;
    end loop;
    
    -- execute 'drop schema "' || schema || '"';
    -- return query select schema, null:text, null::text, 'schema'::text;
end $$ language plpgsql;

-- Example:
-- select * from purgeschema('public');

