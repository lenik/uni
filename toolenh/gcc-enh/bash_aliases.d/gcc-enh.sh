# vim: set filetype=sh :

function gcc-dump-macros() {
    # -dM: -fdump-rtl-mach
    # -E: preprocess only, don't compily
    echo | gcc -dM -E "$@" - | sort
}
