#!/bin/bash  # The shebang is only useful for debug. Don't execute this script.

# This script is used to create a "execute-hook" for bash interpreter
# So a pre/post action could be made before an external program is executed.
#
# Such usage includes:
#   * Prompt user to continue to execute if cmdline contains "SeCReT" argument.

function create_wrapper() {
    local exe="$1"
    local name="${exe##*/}"

    # Only create wrappers for non-builtin commands
    [ `type -t "$name"` = 'file' ] || return

    # echo "Create command wrapper for $exe"
    eval "
    function $name() {\
        if [ \"\$(type -t prefilter)\" = 'function' ]; then \
            prefilter \"$name\" \"\$@\" || return; \
        fi; \
        $exe \"\$@\";
    }"
}
# It's also possible to add pre/post hookers by install
#   [ `type -t \"$name-pre\"` = 'function' ] && \"$name-pre\" \"\$@\"
# into the dynamic generated function body.

function _create_wrappers() {
    local paths="$PATH"
    local path
    local f n

    while [ -n "$paths" ]; do
        path="${paths%%:*}"
        if [ "$path" = "$paths" ]; then
            paths=
        else
            paths="${paths#*:}"
        fi

        # For each path element:
        for f in "$path"/*; do
            if [ -x "$f" ]; then
                # Don't create wrapper for strange command names.
                n="${f##*/}"
                [ -n "${n//[a-zA-Z_-]/}" ] || create_wrapper "$f"
            fi
        done
    done

    unset _create_wrappers  # Remove the installer.
    unset create_wrapper    # Remove the helper fn, which isn't used anymore.
}

_create_wrappers
