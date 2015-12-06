#!/bin/bash
    : ${RCSID:=$Id: - @VERSION@ @DATE@ @TIME@ - $}
    : ${PACKAGE:=@PACKAGE@}
    : ${PROGRAM_TITLE:=Grab meaningful data from generic file}
    : ${PROGRAM_SYNTAX:=[OPTIONS] [--] FILE...}

    . shlib-import cliboot
    option -o --outdir =DIR     "Output directory to save block files."
    option -s --separate        "Save block files in separate dirs."
    option -q --quiet
    option -v --verbose
    option -h --help
    option    --version

    outdir=
    separate=

function setopt() {
    case "$1" in
        -o|--outdir)
            outdir="$2";;
        -s|--separate)
            separate=1;;
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

function main() {
    for f in "$@"; do
        extractor "$f"
}

boot "$@"
