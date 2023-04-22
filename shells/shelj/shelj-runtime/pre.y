script: statements

statement: 
    for_expr
    for_list
    if_else
    expr
    cmd=ARG args ";"
    "{" statements "}"

args:
    | arg
    | args arg
    ;

for_expr:
    "for" "(" expr expr expr ")" statement

for_list:
    "for" "(" ID "in" LIST ")" statement

if_else:
    "if" expr statement
    | "if" expr statement "else" statement

statement:

expr:
    ARG "=" expr
    | expr op=("*" | "/") expr
    | expr op=("+" | "-") expr
    | expr "(" args ")"
    | fn_decl
    | expr
    | statement
    var_ref

fn_decl:
    "function" id=ARG "(" args ")" "{" statements "}"

