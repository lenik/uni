function paths() {
    local var=PATH
    local mesg

    if [ $# -gt 1 ]; then
        for var in "$@"; do
            mesg="Environ $var: "
            echo "$mesg"
            echo "${mesg//?/-}"
            var="${!var}"
            echo -e "${var//:/\\n}"
            echo
        done
    else
        if [ -n "$1" ]; then
            var=$1
        fi
        var="${!var}"
        echo -e "${var//:/\\n}"
    fi
}
