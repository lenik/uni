
    create or replace function trimpadding(s varchar, padding varchar) returns varchar as $$
    declare
        n int := length(s);
        npad int := length(padding);
    begin
        if s is null then return null; end if;
        if padding is null then return s; end if;
        while n >= npad loop
            if substring(s, n - npad + 1, npad) = padding then
                s := substring(s, 1, n - npad);
                n := n - npad;
            else
                exit;
            end if;
        end loop;
        return s;
    end $$ language plpgsql;

    create function salthash(s varchar, n int, salt varchar = 'salty') returns varchar as $$
    declare
        enc varchar;
    begin
        enc := encode(decode(md5(salt || s || salt), 'hex'), 'base64');
        return substring(enc, 1, n);
    end $$ language plpgsql;

