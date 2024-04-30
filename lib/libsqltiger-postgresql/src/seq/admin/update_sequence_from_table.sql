-- Update the nextVal of sequance(s) of specific table(s) to the max id in using.

--drop function update_sequence_from_table(schemaName text, tableName text);

create or replace function update_sequence_from_table(schemaName text, tableName text = null)
    returns table(sequence_name text, last_value bigint) as $$
declare
    row record;
    qTable text;
    qSeq text;
    sql text;
    nextVal bigint;
    curVal bigint;
begin
    
    for row in
        select tab_name, tab_column, seq_name
        from sequsage()
        where tab_schema = schemaName
            and (tableName is null or tab_name = tableName)
    loop
        
        qTable := quote_ident(schemaName) || '.' || quote_ident(row.tab_name);
        qSeq := quote_ident(schemaName) || '.' || quote_ident(row.seq_name);
    
        sql := format('select max(%s) + 1 from %s', quote_ident(row.tab_column), qTable);
        execute sql into nextVal;

        sql := format('select setval(''%s'', %s)', qSeq, nextVal);
        execute sql into curVal;

        raise notice 'sequence % => %', qSeq, curVal;

        return query select row.seq_name, curVal;
    end loop;
end $$ language plpgsql;

-- Example:
-- select * from update_sequence_from_table('lily', 'user');
-- select * from update_sequence_from_table('lily');
