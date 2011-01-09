#!/bin/bash

function showHelp() {
    echo "Usage: "
    echo "    PROGRAM.xj [ARGS]"
}

function findJava() { #
    _JAVA="${_JAVA=java}"
    JAVA=
    for c in \
            "${JAVA=/opt/jre/bin/$_JAVA}" \
            "$JAVA_HOME/jre/bin/$_JAVA" \
            "$JAVA_HOME/bin/$_JAVA" \
            ; do
        if [ -x "$c" ]; then
            JAVA="$c"
            return 0
        fi
    done
    if which "$_JAVA" >/dev/null; then
        JAVA="$_JAVA"
        return 0
    fi
    echo "No Java Runtime Environment found, please reinstall JRE (For Lenix)"
    exit 1
}

function loadLibraries() { #
    local k v
    if [ -z "$_lib_loaded" ]; then
        if [ -f /lib/libraries.ini ]; then
            while read k _ v; do
                eval "lib_$k=\"$v\""
            done </lib/libraries.ini
        fi
        _lib_loaded=1
    fi
}

function addLibrary() { # (libfile | libname)
    local ext path ref val
    for f in "$@"; do
        ext="${f##*.}"
        if [ -f "$f" ]; then
            path="$f"
        else
            if [ $ext = "jar" ]; then
                for libdir in ${LIBPATH/:/ }; do
                    if [ -f "$libdir/$f" ]; then
                        path="$libdir/$f"
                    fi
                done
            else
                loadLibraries
                ref="lib_$f"
                val="${!ref}"
                if [ -n "$val" ]; then
                    added="libadded_$f"
                    if [ -z "${!added}" ]; then
                        addLibrary "$val"
                        eval "$added=1"
                    fi
                fi
                return
            fi
        fi
        if [ -n "$path" ]; then
            echo Path: $path
            LIBS="$LIBS:$path"
        fi
    done
}

function collectClassPath() { # (PI-FILE | jar | dir)
    local PI
    local k nk v
    if [ -f "$1.PI" ]; then
        PI="$1.PI"
    elif [ -f "$1" ]; then
        PI=/tmp/PACKINFO-$$.PI
        unzip -p "$1" META-INF/PACKINFO.PI >$PI
    elif [ -f "$1/META-INF/PACKINFO.PI" ]; then
        # assert [ -d $1 ]
        PI="$1/META-INF/PACKINFO.PI"
    else
        return
    fi
    while read k v; do
        if [ -n "$k" ]; then
            k="${k%:}"
            k="${k//-/_}"
            case "$k" in
              booter)
                bootClass="$v";;
              lib)
                for i in ${v//,/ }; do
                    addLibrary "$i"
                done;;
              require)
                for i in ${v//,/ }; do
                    collectClassPath "$i"
                done;;
              vmargs)
                JAVA_OPTS="$JAVAOPTS $v";;
            esac
        fi
    done <"$PI"
}

function loadManifest() { # (PI-FILE | jar | dir)
    echo Load manifest...
}

function main() {
    findJava

    xjFile="$1"
    shift

    mainClass=
    bootClass=
    loadManifest "$xjFile"

    if [ -z "$mainClass" ]; then
        echo "Error: main class is unknown"
        showHelp
        exit 1
    fi

    if [ -z "$LIBPATH" ]; then
        LIBPATH=/lib:./lib
    fi
    LIBS=$xjFile

    collectClassPath "$xjFile"

    # echo "LIBS=$LIBS"
    export CLASSPATH="$LIBS"

    "$JAVA" $JAVA_OPTS $bootClass $mainClass $*
}

# for arg in "$@"; do echo "Arg: $arg"; done
main "$@"
