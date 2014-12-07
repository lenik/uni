--drop function columnstat();

create or replace function columnstat(schema varchar)
    returns table(tab varchar, col varchar, valc int, vals text[], cnts bigint[]) as $$
declare
    qtabname varchar;
    qcolname varchar;
    v record;
    u record;
    sql varchar;
    cnt int;
    vals text[];
    cnts bigint[];
begin
    for v in select * from information_schema.columns
            where table_schema = schema
            order by table_name, column_name
    loop
        qtabname := '"' || schema || '"."' || v.table_name || '"';
        qcolname := '"' || v.column_name || '"';
        
        sql := 'select count(distinct ' || qcolname || ') "cnt" from ' || qtabname;
        execute sql into cnt;

        if cnt < 10 then
            vals := array[]::text[];
            cnts := array[]::int[];
            sql := 'select ' || qcolname || '::text val'
                    || ', count('   || qcolname || ') cnt'
                 -- || ', pg_typeof(' || qcolname || ') typ'
                    || ' from '     || qtabname
                    || ' where '    || qcolname || ' is not null'
                    || ' group by ' || qcolname
                    || ' order by ' || qcolname || '::text';
            for u in execute sql loop
                vals := vals || array[u.val];
                cnts := cnts || array[u.cnt];
            end loop;
        else
            vals := null;
            cnts := null;
        end if;
        
        return query select
                cast(v.table_name as varchar), 
                cast(v.column_name as varchar), 
                cnt, vals, cnts;
    end loop;
end
$$ language plpgsql;

-- Examples:
--  select * from columnstat() where valc > 0 order by valc;

