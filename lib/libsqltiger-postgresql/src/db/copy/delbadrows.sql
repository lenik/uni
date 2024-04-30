
create or replace procedure delbadrows(q_target varchar, q_refer varchar, dry_run boolean = false) as $$
declare
    target_dot int = position('.' in q_target);
    target_schema varchar = case when target_dot = 0 then null else substring(q_target, 1, target_dot - 1) end;
    target_table varchar = case when target_dot = 0 then q_target else substring(q_target, target_dot + 1) end;
    
    refer_dot int = position('.' in q_refer);
    refer_schema varchar = case when refer_dot = 0 then q_refer else substring(q_refer, 1, refer_dot - 1) end;
    refer_table varchar = case when refer_dot = 0 then target_table else substring(q_refer, refer_dot + 1) end;
    
    row record;
    sql varchar;
    sql2 varchar = null;
    count bigint;
begin

    if refer_dot = 0 then
        for row in select count(1) as n, min(table_schema) table_schema
                from information_schema.tables
                where table_name = q_refer
        loop
            if count = 1 then
                refer_schema := row.table_schema;
                refer_table := q_refer;
            end if;
        end loop;
    end if;

    sql := 'delete from ' || q_target || ' using ' || q_target || ' a';

    for row in select
            a.table_schema, 
            a.constraint_name, 
            a.table_name, 
            kcu.column_name,
            ccu.table_schema as parent_schema,
            ccu.table_name as parent_table,
            ccu.column_name as parent_column 
        from information_schema.table_constraints as a 
            join information_schema.key_column_usage as kcu
                on a.constraint_name = kcu.constraint_name
                and a.table_schema = kcu.table_schema
            join information_schema.constraint_column_usage as ccu
				on a.table_schema = ccu.table_schema
                and ccu.constraint_name = a.constraint_name
        where a.constraint_type = 'FOREIGN KEY'
            and a.table_schema = refer_schema
            and a.table_name = refer_table
    loop
	    sql := sql || ' left join ' || row.parent_schema || '.' || row.parent_table
            || ' on a.' || row.column_name || ' = ' || row.parent_table || '.' || row.parent_column;
        
        if sql2 is null then
            sql2 := ' where ';
        else
            sql2 := sql2 || ' or ';
        end if;
        
        sql2 := sql2 || row.parent_table || '.' || row.parent_column || ' is null';
    end loop;

	sql := sql || ' ' || sql2;
    raise notice '%', sql;

    if not dry_run then
        execute sql;
        get diagnostics count = row_count;
        raise notice 'deleted % rows from %', count, q_target;
    end if;

end $$ language plpgsql;
