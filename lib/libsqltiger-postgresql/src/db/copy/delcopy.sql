
create or replace function csvdir() returns varchar as $$
begin
    return '/mnt/data/csv';
end $$ language plpgsql;

create or replace procedure delcopy(q_src varchar, q_dst varchar, del boolean = true, dry_run boolean = false) as $$
declare
    src_dot int = position('.' in q_src);
    src_schema varchar = case when src_dot = 0 then null else substring(q_src, 1, src_dot - 1) end;
    src_table varchar = case when src_dot = 0 then q_src else substring(q_src, src_dot + 1) end;
    
    dst_dot int = position('.' in q_dst);
    dst_schema varchar = case when dst_dot = 0 then q_dst else substring(q_dst, 1, dst_dot - 1) end;
    dst_table varchar = case when dst_dot = 0 then src_table else substring(q_dst, dst_dot + 1) end;

    csvdir varchar;    
    csvfile varchar;
    sql varchar;
begin

    if del then
        call delbadrows(q_src, q_dst, dry_run);
    end if;
    
    csvdir := csvdir();
    csvfile := csvdir || '/' || src_table || '.csv';

    raise notice 'copy table % to %', q_src, csvfile;
    if not dry_run then
        sql := 'copy ' || q_src || ' to ' || quote_literal(csvfile) || ' with csv header';
        execute sql;
    end if;

    raise notice 'copy table % from %', q_dst, csvfile;
    if not dry_run then
        sql := 'copy ' || q_dst || ' from ' || quote_literal(csvfile) || ' with csv header';
        execute sql;
    end if;

end $$ language plpgsql;
