# vim: set filetype=sh :

function _pg_version() {
    _log1 "CoolSQL/PostgreSQL Virtual Driver 1.0"
}

function _pg_exec() {
    local cmdv=()
    if [ -n "$opt_exec_cmd" ]; then
        cmdv[0]="$opt_exec_cmd"
    else
        cmdv[0]="$1"
        shift
    fi
    
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

    _log1 Execute "${cmdv[@]}" "$@"
    "${cmdv[@]}" "$@"
}

function _pg_psql() {
    _pg_exec psql "$@"
}

function _pg_console() {
    _pg_psql
}

function _pg_list_db() {
    _pg_psql -t -l | cut -d\| -f1 | sed -e 's/ //g' | grep -v '^$'
}

function _pg_list_table() {
    _pg_psql -A -t -F: -c '\d' | cut -d: -f2
}

function _pg_eval() {
    _pg_psql -c "$@"
}

function _pg_file() {
    _pg_psql -f "$@"
}
