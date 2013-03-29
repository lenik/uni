# vim: set filetype=sh :

function coolpgsql_version() {
    _log1 "CoolSQL/PostgreSQL Virtual Driver 1.0"
}

function coolpgsql_psql() {
    local cmdv=(psql)
    local cmdn=${#cmdv[@]}

    #[ -n "$db_host" ] && export PGHOST="$db_host"
    #[ -n "$db_port" ] && export PGPORT="$db_port"
    #[ -n "$db_name" ] && export PGDATABASE="$db_name"
    #[ -n "$db_user" ] && export PGUSER="$db_user"
    [ -n "$db_pass" ] && export PGPASSWORD="$db_pass"

    [ -n "$db_host" ] && cmdv[cmdn++]="-h$db_host"
    [ -n "$db_port" ] && cmdv[cmdn++]="-p$db_port"
    [ -n "$db_name" ] && cmdv[cmdn++]="-d$db_name"
    [ -n "$db_user" ] && cmdv[cmdn++]="-U$db_user"
    [ -n "$db_pass" ] && export PGPASSWORD="$db_pass"

    if [ ! -f "$HOME/.pgpass" ]; then
        _warn "The provided password in $conn_rc is ignored."
        _warn "You need to maintain your postgresql passwords in $HOME/.pgpass file"
        _warn "Each line should be in the format: "
        _warn "    hostname:port:database:username:password"
    fi

    "${cmdv[@]}" "$@"
}

function coolpgsql_console() {
    coolpgsql_psql
}

function coolpgsql_list_db() {
    coolpgsql_psql -t -l | cut -d\| -f1 | sed -e 's/ //g' | grep -v '^$'
}

function coolpgsql_list_table() {
    coolpgsql_psql -A -t -F: -c '\d' | cut -d: -f2
}

function coolpgsql_eval() {
    coolpgsql_psql -c "$@"
}

function coolpgsql_file() {
    coolpgsql_psql -f "$@"
}
