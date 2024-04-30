--drop function update_table_restart(schema_name text, table_name text);

create or replace function update_table_restart(schema_name text, table_name text = null)
    returns table(last_value bigint) as $$
declare
    q_table text = quote_ident(schema_name) || '.' || quote_ident(table_name);
    q_seq text = quote_ident(schema_name) || '.' || quote_ident(table_name || '_seq');
    row record;
begin
    for row in
        select tab_name, tab_column, start_value
            from sequsage()
            where tab_schema = schema_name
                and (table_name is null or tab_name = table_name);
    loop
        raise notice 'Re-numbering table %(%) from %...', 
            row.tab_name, row.tab_column, row.start_value;
        
        execute 'alter sequence ' || q_seq || ' restart';
        
        execute 'update ' || q_table 
            || ' set ' || quote_ident(row.tab_column) || ' = default'
            || ' where ' || quote_ident(row.tab_column) || ' >= ' || seq_start;

        return query execute 'select last_value from ' || q_seq;

    end loop;
end $$ language plpgsql;

-- Example:
-- select * from update_table_restart('lily', 'user');
