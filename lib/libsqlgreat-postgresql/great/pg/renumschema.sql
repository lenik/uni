--drop function renumschema(schema text);

create or replace function renumschema(schema text)
    returns table(tab_name text, last_value bigint) as $$
declare
    v record;
    lastval bigint;
begin
    for v in select * from sequsage() where tab_schema = schema
    loop
        select r.last_value into lastval from renum(schema, v.tab_name) r;
        return query select v.tab_name, lastval;
    end loop;
end $$ language plpgsql;

-- Example:
-- select * from renumschema('public');

