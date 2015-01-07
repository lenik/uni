--drop function prevnext(schema text, tabname text, id bigint);

create or replace function prevnext(schema text, tabname text, id bigint)
    returns table(prev bigint, next bigint) as $$
declare
    v record;
    qtab text;
    prev bigint;
    next bigint;
begin
    qtab := '"' || schema || '"."' || tabname || '"';
    execute 'select max(id) from ' || qtab || ' where id < ' || id into prev;
    execute 'select min(id) from ' || qtab || ' where id > ' || id into next;
    return query select prev "prev", next "next";
end $$ language plpgsql;

-- Example:
-- select * from prevnext('public', 'user', 100);


