--drop function renum(schema text, table_name text);

create or replace function renum(schema text, table_name text)
    returns table(last_value bigint) as $$
declare
    qtabname text;
    qidcol text;
    qseqname text;
    idfrom bigint;
begin
    qtabname := '"' || schema || '"."' || table_name || '"';
    qseqname := '"' || schema || '"."' || table_name || '_seq"';
    
    select tab_column, start_value into qidcol, idfrom
        from sequsage()
        where tab_schema = schema and tab_name = table_name;
    
    qidcol = '"' || qidcol || '"';
    
    execute 'alter sequence ' || qseqname || ' restart';
    execute 'update ' || qtabname || ' set ' || qidcol || ' = default'
        || ' where ' || qidcol || ' >= ' || idfrom;

    return query execute 'select last_value from ' || qseqname;
end $$ language plpgsql;

-- Example:
-- select * from renum('public', 'user');

