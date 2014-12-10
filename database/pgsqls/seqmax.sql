-- Update the nextval of sequance(s) of specific table(s) to the max id in using.

--drop function seqmax(schema text, tabname text);

create or replace function seqmax(schema text, tabname text)
    returns table(sequence_name text, last_value bigint) as $$
declare
    v record;
    qtab text;
    qseq text;
    qcol text;
    val bigint;
begin
    
    for v in select tab_name, tab_column, seq_name
            from sequsage()
            where tab_schema=schema and (tabname is null or tab_name=tabname)
    loop
        
        qtab := '"' || schema || '"."' || v.tab_name || '"';
        qseq := '"' || schema || '"."' || v.seq_name || '"';
        qcol = '"' || v.tab_column || '"';
    
        execute E'select setval(\'' || qseq || E'\', (select max(' || qcol || ') from ' || qtab || '))' into val;
        return query select v.seq_name, val;
    end loop;
end $$ language plpgsql;

-- Example:
-- select * from seqmax('public', 'user');
-- select * from seqmax('public', null);

