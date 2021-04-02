# vim: set filetype=sh :

fDefaults=hpuk
    f_clusterdb=hpukD
    f_createdb=hpuk
    f_createuser=hpuk
    f_dropdb=hpuk
    f_dropuser=hpuk
    f_initdb=hpuk
    f_oid2name=hpukd
    f_pg_archivecleanup=hpuk
    f_pg_basebackup=hpukd
    f_pgbench=hpuk
    f_pg_controldata=hpuk
    f_pg_ctl=hpuk
    f_pg_dump=hpukd
    f_pg_dumpall=hpukl
    f_pg_isready=hpukd
    f_pg_receivewal=hpukd
    f_pg_recvlogical=hpukd
    f_pg_resetwal=hpuk
    f_pg_restore=hpukd
    f_pg_rewind=hpuk
    f_pg_standby=hpuk
    f_pg_test_fsync=hpuk
    f_pg_test_timing=hpuk
    f_pg_upgrade=hpuk
    f_pg_verify_checksums=hpuk
    f_pg_waldump=hpuk
    f_postgres=hpuk
    f_postmaster=hpuk
    f_psql=hpukd
    f_reindexdb=hpuk
    f_vacuumdb=hpuk
    f_vacuumlo=hpuk

function _pg_version() {
    _log1 "CoolSQL/PostgreSQL Virtual Driver 1.0"
}

function _pg_exec() {
    local cmdv=()
    local cmdn=0
    local cmdname=
        if [ -n "$opt_exec_cmd" ]; then
            cmdname="$opt_exec_cmd"
        else
            cmdname="$1"
            shift
        fi
        cmdname="${cmdname##*/}"
    cmdv[cmdn++]="$cmdname"
    
    local fRef=f_$cmdname
    local fchars=${!fRef}
    if [ -z "$fchars" ]; then
        fchars=$fDefaults
    fi
    local fchars_n=${#fchars}
    
    for ((i = 0; i < fchars_n; i++)); do
        local fchar=${fchars:i:1}
        case "$fchar" in
            h)
                if [ -n "$db_host" ]; then
                    export PGHOST="$db_host"
                    cmdv[cmdn++]="-h$db_host"
                fi;;
            p)
                if [ -n "$db_port" ]; then
                    export PGPORT="$db_port"
                    cmdv[cmdn++]="-p$db_port"
                fi;;
            u)
                if [ -n "$db_user" ]; then
                    export PGUSER="$db_user"
                    cmdv[cmdn++]="-U$db_user"
                fi;;
            k)
                if [ -n "$db_pass" ]; then
                    export PGPASSWORD="$db_pass"
                fi
                if [ ! -f "$HOME/.pgpass" ]; then
                    _warn "The provided password in $conn_rc is ignored."
                    _warn "You need to maintain your postgresql passwords in $HOME/.pgpass file"
                    _warn "Each line should be in the format: "
                    _warn "    hostname:port:database:username:password"
                fi
                ;;
            d|D)
                if [ -n "$db_name" ]; then
                    export PGDATABASE="$db_name"
                    cmdv[cmdn++]="-d$db_name"
                fi;;
            l)
                if [ -n "$db_name" ]; then
                    export PGDATABASE="$db_name"
                    cmdv[cmdn++]="-l$db_name"
                fi;;
            *)
                _quit "error fchar: $fchar."
                ;;
        esac
    done
    
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
