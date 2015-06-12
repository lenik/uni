-- drop function valdist(text, text, text);

create or replace function valdist(table_name text, tcol text, table_schema text default 'public')
    returns table(_val text, count bigint) as $$
declare
    qtabname varchar;
    qtcol varchar;
    v record;
    sql text;
begin
    qtabname := '"' || table_schema || '"."' || table_name || '"';
    qtcol := '"' || tcol || '"';
    sql := 'select '
            || qtcol || '::text "_val", '
            || 'count(*)::bigint'
        || ' from ' || qtabname
        || ' group by '
            || qtcol;
    return query execute sql;
end
$$ language plpgsql;

-- Examples:
--  select * from valdist('person', 'uid');

