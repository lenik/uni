create or replace function lastIndexOf(haystack varchar, needle varchar) returns integer as $$
declare
    n int := length(haystack);
    m int := length(needle);
    rhaystack varchar := reverse(haystack);
    rneedle varchar := reverse(needle);
    rpos int;
begin
    rpos := position(rneedle in rhaystack);
    if rpos = 0 then
        return 0;
    end if;
    return n - rpos + 1 - (m - 1);
end $$ language plpgsql;

create or replace function after_last(s varchar, delim varchar) returns varchar as $$
declare
    lastpos int := lastIndexOf(s, delim);
begin
    if lastpos = 0 then
        return s;
    end if;
    return substring(s, lastpos + length(delim));
end $$ language plpgsql;


create or replace procedure make_samples_view(
        col_name varchar,
        view_schema varchar = null,
        materialized boolean = false, 
        short_view_names boolean = true,
        schema_name varchar = null,
        n_max int = 100) as $$
declare
    row record;
    simple_name varchar;
    view_type varchar := 'view';
    view_name varchar;
    ddl varchar;
begin
    for row in select a.table_schema, a.table_name, c.column_name
            from information_schema.columns c
                left join information_schema.tables a 
                    on c.table_schema = a.table_schema
                    and c.table_name = a.table_name
            where a.table_type in ('BASE TABLE')
                and (schema_name is null or a.table_schema = schema_name)
                and c.column_name = col_name
            order by a.table_schema, a.table_name
    loop
        if short_view_names then
            simple_name := after_last(row.table_name::varchar, '_');
        else
            simple_name := row.table_name::varchar;
        end if;
        view_name := coalesce(view_schema, row.table_schema) || '.';
        if materialized then
            view_type := 'materialized view';
            view_name := view_name || 'msamples_' || simple_name;
        else
            view_name := view_name || 'samples_' || simple_name;
        end if;

        raise notice '%', 'Recreate ' || view_type || ' ' || view_name;
        
        ddl := 'drop ' || view_type || ' if exists ' || view_name;
        execute ddl;

        ddl := 'create ' || view_type || ' ' || view_name || ' as'
            || ' select djxh, count(1) as n'
            || ' from ' || row.table_schema || '.' || row.table_name || ' a'
            || ' group by djxh'
            || ' order by n desc'
            || ' limit ' || n_max;
        
        execute ddl;
    end loop;
end $$ language plpgsql;

create or replace procedure drop_samples_view(
        col_name varchar, 
        view_schema varchar = null,
        materialized boolean = false, 
        short_view_names boolean = true,
        schema_name varchar = null) as $$
declare
    row record;
    simple_name varchar;
    view_type varchar := 'view';
    view_name varchar;
    ddl varchar;
begin
    for row in select a.table_schema, a.table_name, c.column_name
            from information_schema.columns c
                left join information_schema.tables a 
                    on c.table_schema = a.table_schema
                    and c.table_name = a.table_name
            where a.table_type in ('BASE TABLE')
                and (schema_name is null or a.table_schema = schema_name)
                and c.column_name = col_name
            order by a.table_schema, a.table_name
    loop
        if short_view_names then
            simple_name := after_last(row.table_name::varchar, '_');
        else
            simple_name := row.table_name::varchar;
        end if;
        view_name := coalesce(view_schema, row.table_schema) || '.';
        if materialized then
            view_type := 'materialized view';
            view_name := view_name || 'msamples_' || simple_name;
        else
            view_name := view_name || 'samples_' || simple_name;
        end if;

        raise notice '%', 'Drop ' || view_type || ' ' || view_name;
        
        ddl := 'drop ' || view_type || ' if exists ' || view_name;
        execute ddl;
    end loop;
end $$ language plpgsql;

call make_samples_view('djxh', 'public');
