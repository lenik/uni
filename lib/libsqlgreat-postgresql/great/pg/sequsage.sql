-- drop view v_sequsage;

create or replace view v_sequsage as
    select c.table_schema, c.table_name, c.column_name, c.is_nullable, s.sequence_name, s.start_value
    from information_schema.columns c
        left join information_schema.sequences s
            on substr(c.column_default, 9 + 1, length(c.column_default) - 9 - 12) = s.sequence_name
    where c.column_default like 'nextval(''%''::regclass)';

-- drop function sequsage();

create or replace function sequsage()
    returns table(tab_schema text,
                  tab_name text,
                  tab_column text,
                  is_nullable bool,
                  seq_schema text,
                  seq_name text,
                  start_value bigint) as $$
declare
    v record;
    s text;
    pos int;
    s1 text;
    s2 text;
    val bigint;
begin
    for v in select * from information_schema.columns
            where column_default like 'nextval(''%''::regclass)'
            order by table_schema, table_name
    loop
        s := substr(v.column_default, 9 + 1, length(v.column_default) - 9 - 12);
        pos := strpos(s, '.');
        if pos = 0 then
            s1 := 'public';
            s2 := s;
        else
            s1 := left(s, pos - 1);
            s2 := substr(s, pos + 1);
        end if;
        
        select s.start_value into val from information_schema.sequences s
            where sequence_schema = s1 and sequence_name = s2;
            
        return query select
            v.table_schema::text,
            v.table_name::text,
            v.column_name::text,
            v.is_nullable = 'YES',
            s1::text,
            s2::text,
            val;
    end loop;
end $$ language plpgsql;

-- Example:
-- select * from sequsage();

