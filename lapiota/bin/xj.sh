#!/bin/bash
    : ${RCSID:=$Id: - @VERSION@ @DATE@ @TIME@ - $}
    : ${PROGRAM_TITLE:="(XJ)Executable Java Launcher"}
    : ${PROGRAM_SYNTAX:="[OPTIONS] [--] FILE.XJ [ALIAS|'?'] ARGUMENTS"}

    . shlib-import cliboot
    option -q --quiet       "Repeat to get less info"
    option -v --verbose     "Repeat to get more info"
    option -h --help        "Show this help page"
    option    --version     "Print the version info"

    _ps=';'
    boot_class=
    main_class=
    JAVA_OPTS=

function setopt() {
    case "$1" in
        -h|--help)
            help $1; exit;;
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

function findJava() { #
    _JAVA="${_JAVA=java}"
    for c in \
            "/opt/jre/bin/$_JAVA" \
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

function expandVars() { # var_prefix k=v k=v ...
    local prefix="$1"
    shift
    for e in "$@"; do
        # if entry doesn't contain '=', then "e=e" will be set.
        k="${e%%=*}"
        v="${e#*=}"
        varname="$prefix$k"
        _log2 "  $varname=$v"
        eval "$varname=\"$v\""
    done
}

function loadMF() { # var_prefix NAME.MF (PI-FILE | jar | dir)
    local prefix="$1"
    local mfname="$2"
    local mffile
    local src="$3"
    local tmp
    _log2 "Load manifest of $src"
    if [ -f "$mfname" ]; then
        mffile="$mfname"
    elif [ -d "$src" ]; then
        if [ -f "$src/$mfname" ]; then
            mffile="$src/$mfname"
        else
            _log2 "Directory $src doesn't contain $mfname"
            return
        fi
    elif [ -f "$src" ]; then
        tmp=$TEMP/loadMF-$$-$RANDOM.MF
        if unzip -p "$src" "$mfname" >$tmp 2>/dev/null; then
            mffile="$tmp"
        else
            _log2 "Archive $src doesn't contain $mfname"
            rm "$tmp"
        fi
        if [ -z "$mffile" ]; then return; fi
    else
        echo "Error: src isn't existed: $src"
        return
    fi

    _log2 "Parsing $mfname of $3..."
    local IFS=:
    local varname
    while read k v; do
        if [ "${k:0:1}" = ' ' ]; then
            append="${!varname}"
            v="${k:1}$v"
        else
            append=
            k="${k//[^a-zA-Z_0-9]/_}"
            # k="$(echo $k|tr [:upper:] [:lower:])"
            while [ "${k: -1}" = ' ' ]; do k="${k:0:${#k}-1}"; done
            if [ -z "$k" ]; then
                # following are named sections or digests, ignore them all.
                break
            fi
            varname="$prefix$k"
        fi
        while [ "${v:0:1}" = ' ' ]; do v="${v:1}"; done
        v="$append$v"
        v="${v//\\/\\\\}"
        v="${v//\"/\\\"}"
        _log2 "  $varname=$v"
        eval "$varname=\"$v\""
    done <"$mffile"
    if [ -n "$tmp" ]; then rm "$tmp"; fi
}

function loadLibraries() { #
    local k v
    if [ -z "$_lib_loaded" ]; then
        for libdir in ${JARPATH//$_ps/ }; do
            local inifile="$libdir/libraries.ini"
            if [ -f "$inifile" ]; then
                _log2 "Config libraries in $inifile..."
                while read k _ v; do
                    if [ -z "$k" ]; then continue; fi
                    _log2 "  Lib $k=$v"
                    eval "lib_$k=\"$v\""
                done <"$inifile"
            else
                _log2 "No config file in $libdir"
            fi
        done
        _lib_loaded=1
    fi
}

function addJarPath() { # PATH ...
    local path
    for path in "$@"; do
        path="$(cygpath -aw $path)"
        _log2 "Add JAR Path: $path"
        if [ -z "$JARPATH" ]; then
            JARPATH="$path"
        else
            JARPATH="$JARPATH$_ps$path"
        fi
    done
}

function addLibrary() { # (libfile | libname)
    local lib ext path ref val
    for lib in "$@"; do
        ext="${lib##*.}"
        if [ -f "$lib" ]; then
            path="$(cygpath -aw $lib)"
        elif [ $ext = "jar" ]; then
            for libdir in ${JARPATH//$_ps/ }; do
                if [ -f "$libdir/$lib" ]; then
                    path="$libdir/$lib"
                fi
            done
        else
            loadLibraries
            ref="lib_$lib"
            val="${!ref}"
            if [ -n "$val" ]; then
                _log2 "Library Alias $lib => $val"
                added="libadded_$lib"
                if [ -z "${!added}" ]; then
                    eval "$added=1"
                    addLibrary "$val"
                fi
            else
                _log2 "Library isn't defined: $lib"
            fi
            continue
        fi
        if [ -n "$path" ]; then
            _log2 "Add Library: $path"
            if [ -z "$LIBS" ]; then
                LIBS="$path"
            else
                LIBS="$LIBS$_ps$path"
            fi
        fi
    done
}

function main() {
    if [ $# -lt 1 ]; then help; exit; fi
    xj_file="$1"
    xj_dir="${xj_file%/*}"
    if [ "$xj_dir" = "$xj_file" ]; then xj_dir=.; fi
    shift

    loadMF mf_ META-INF/MANIFEST.MF "$xj_file"
    if [ -n "$mf_VM_Arguments" ]; then
        JAVA_OPTS="$JAVA_OPTS $mf_VM_Arguments"
    fi
    if [ -z "$boot_class" ]; then
        boot_class="$mf_Boot_Class"
    fi

    expandVars _ma_ ${mf_Main_Aliases//,/ }

    # if not specified, and no default main-class, then ARG[1] is used
    if [ -z "$main_class" ]; then
        if [ -n "$mf_Main_Class" ]; then
            _log1 "MainClass: $mf_Main_Class"
            main_class="$mf_Main_Class"
        else
            if [ $# = 0 ]; then
                main_class='?'
            else
                main_class="$1"
                shift
            fi
        fi
    fi
    # assert main_class != ''

    if [ -n "$mf_Main_Aliases" ]; then
        mavar="_ma_${main_class}"
        if [ -n "${!mavar}" ]; then
            _log2 "Expand main alias $main_class => ${!mavar}"
            main_class="${!mavar}"
        fi
    fi

    if [ "$main_class" = '?' ]; then
        echo Available main aliases:
        set | grep ^_ma_ | sed 's/^_ma_/    /g'
        exit 1
    fi

    _log2 "main-class: $main_class"

    if [ -z "$JARPATH" ]; then
        addJarPath . "$LAM_KALA/lib" "$LAM_KALA/usr/lib" "$JAVA_LIB"
    fi
    if [ "$xj_dir" != '.' ]; then
        addJarPath "$xj_dir"
    fi
    _log2 "JARPATH=$JARPATH"

    LIBS=
    addLibrary "$xj_file"
    if [ -n "$mf_Library_Path" ]; then
        addLibrary ${mf_Library_Path//,/ }
    fi

    _log2 "CLASSPATH=$LIBS"
    export CLASSPATH="$LIBS"

    findJava
    "$JAVA" $JAVA_OPTS $boot_class $main_class $*
    _exit=$?
    if [ $_exit != 0 ]; then
        echo "Program exit abnormally: $_exit"
        echo "Press any key to continue..."
        read -n 1
    fi
}

boot "$@"
