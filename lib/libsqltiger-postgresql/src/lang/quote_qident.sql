
create or replace function quote_qident(qname varchar)
    returns varchar as $$
declare
    vec varchar[] = string_to_array(qname, '.');
    item varchar;
    buf varchar;
begin
    foreach item in array vec
    loop
        if buf is null then
            buf := '';
        else
            buf := buf || '.';
        end if;
        buf := buf || quote_ident(item);
    end loop;
    return buf;
end;
$$ language plpgsql;

