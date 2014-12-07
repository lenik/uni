-- Create alias-view for specific table.

--drop function mkaview();

create or replace function mkaview(schema varchar, tab varchar, prefix varchar)
    returns table(orig varchar, alias varchar) as $$
declare
    qtab varchar;
    qview varchar;
    qcol varchar;
    qacol varchar;
    v record;
    sql varchar;
    len int;
begin
    qtab := '"' || schema || '"."' || tab || '"';
    qview := '"' || schema || '"."av' || prefix || tab || '"';
    sql := 'create view ' || qview || ' as select';
    
    for v in select * from information_schema.columns
            where table_schema = schema and table_name = tab
    loop
        qcol := '"' || v.column_name || '"';
        qacol := '"' || prefix || v.column_name || '"';
        
        sql := sql || ' ' || qcol || ' as ' || qacol;
        sql := sql || ', ';
    
        return query select qcol::varchar, qacol::varchar;
    end loop;

    len := length(sql);
    sql := left(sql, len - 2); -- trim the trailing ', '.
    sql := sql || ' from ' || qtab;

    raise info 'Execute SQL: %', sql;
    execute sql;
end
$$ language plpgsql;

-- Examples:
-- select * from mkaview('public', 'person', 'p_');
-- select * from avp_person;

