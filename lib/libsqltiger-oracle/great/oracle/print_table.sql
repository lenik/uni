create or replace procedure print_table(query in varchar2)
        authid current_user
        is
    cur integer default dbms_sql.open_cursor;
    colval varchar2(4000);
    status integer;
    desc_tab dbms_sql.desc_tab;
    ncol number;
    nrow number := 1;
begin
    dbms_sql.parse(cur, query, dbms_sql.native);
    dbms_sql.describe_columns(cur, ncol, desc_tab);
    
    for i in 1..ncol loop
        dbms_sql.define_column(cur, i, colval, 4000);
    end loop;
    
    status := dbms_sql.execute(cur);
    
    while (dbms_sql.fetch_rows(cur) > 0) loop
        for i in 1..ncol loop
            dbms_sql.column_value(cur, i, colval);
            dbms_output.put_line(rpad(desc_tab(i).col_name, 30) || ': ' || colval);
        end loop;
        dbms_output.put_line();
        dbms_output.put_line('[ROW:' || nrow || '] -----------------------');
        nrow := nrow + 1;
    end loop;
    
    dbms_sql.close_cursor(cur);
exception
    when others then
        dbms_sql.close_cursor(cur);
        raise;
end print_table;

