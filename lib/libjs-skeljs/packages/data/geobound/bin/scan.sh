#!/bin/bash
    : ${RCSID:=$Id: - @VERSION@ @DATE@ @TIME@ - $}
    : ${PACKAGE:=@PACKAGE@}
    : ${PROGRAM_TITLE:=}
    : ${PROGRAM_SYNTAX:=[OPTIONS] [--] ...}

    . shlib-import cliboot
    option -q --quiet
    option -v --verbose
    option -h --help
    option    --version

function setopt() {
    case "$1" in
        -h|--help)
            help $1; exit;;
        -q|--quiet)
            LOGLEVEL=$((LOGLEVEL - 1));;
        -v|--verbose)
            LOGLEVEL=$((LOGLEVEL + 1));;
        --version)
            show_version; exit;;
        *)
            quit "invalid option: $1";;
    esac
}

service=https://geo.datav.aliyun.com/areas_v3/bound

function main() {
    scan "$@"
}

function scan() {
    local code
    for code in "$@"; do
        case $code in
            *00)
                : ;;
            *)
                continue;;
        esac
        
        local file=${code}_full.json
        if [ ! -f "$file" ]; then
            local url=$service/$file
            _log1 "download $file..."
            wget -q "$url"
        fi
        
        local codes=()
        if ! codes=( $(jq -r .features[].properties.adcode "$file" ) ); then
            _error "error parse $file"
            continue
        fi
        
        local child
        for child in "${codes[@]}"; do
            scan $child
        done
    done
}

boot "$@"
