#!/bin/bash
    RCSID='$Id: .sh 921 2009-04-29 23:50:10Z Shecti $'
    short_opts="hqvr:s:p:"
    long_opts="help,quiet,verbose,version"
    . $LAM_KALA/lib/sh/cliboot

    SVNSERVE='C:\Program Files\CollabNet Subversion Server\svnserve.exe'
    PORT=3690

function version() {
    parse_id "$RCSID"
    echo "[$BASENAME] Install new repository with separated svn port"
    echo "Written by Lenik, Version 0.$rcs_rev, Last updated at $rcs_date"
}

function help() {
    version
    echo
    echo "Syntax: "
    echo "    $0 [OPTION] [--] ROOT-PATH"
    echo
    echo "Options: "
    #echo "    -r, --root=PATH         root path of the new repository"
    echo "    -s, --svnserve=PATH     svnserve executable to use"
    echo "    -p, --port=PORT         listen at specified PORT"
    echo "    -q, --quiet             repeat to get less info"
    echo "    -v, --verbose           repeat to get more info"
    echo "    -h, --help              show this help page"
    echo "        --version           print the version info"
}

function setopt() {
    case "$1" in
        -r|--root)
            ROOT="$2";;
        -s|--svnserve)
            SVNSERVE="$2";;
        -p|--port)
            PORT="$2";;
        -h|--help)
            help; exit;;
        -q|--quiet)
            LOGLEVEL=$((LOGLEVEL - 1));;
        -v|--verbose)
            LOGLEVEL=$((LOGLEVEL + 1));;
        --version)
            version; exit;;
        *)
            quit "invalid option: $1";;
    esac
}

function main() {
    ROOT="$1"

    if [ ! -x "$SVNSERVE" ]; then
        echo "Bad svnserve executable: $SVNSERVE"
        exit 1
    fi
    if [ ! -d "$ROOT" ]; then
        echo "Repository $ROOT isn't existed"
        exit 2
    fi

    SERVICE="svnserve_$PORT"
    echo "Service name: $SERVICE"
    echo
    echo Remove existing svnserve...
    sc delete "$SERVICE"
    echo
    echo "Create service $SERVICE..."
    sc create "$SERVICE" \
        start=          auto \
        obj=            LocalSystem \
        DisplayName=    "SVN#$PORT -> $ROOT" \
        binPath=        "\"$SVNSERVE\" --service -r \"$ROOT\" --listen-port \"$PORT\""
}

boot "$@"
