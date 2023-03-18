alias mvn='coolmvn'

MAVENOPTS_SKIPTEST=(-Dmaven.test.skip=true -DskipTests)

function mvnlist() {
    local cmd="$1"
    shift

    local opts=()
    while true; do
        case "$1" in
            -B|--batch-mode|-C|--strict-checksums|-c|--lax-checksums)
                opts+=("$1")
                shift 1;;
            -D*)
                opts+=("$1")
                shift 1;;
            --define)
                opts+=("$1" "$2")
                shift 2;;
            -e|--errors|-q|--quiet|-X|--debug)
                opts+=("$1")
                shift 1;;
            -fae|-ff|-fn|--fail-at-end|--fail-fast|--fail-never)
                opts+=("$1")
                shift 1;;
            -N|--non-recursive)
                opts+=("$1")
                shift 1;;
            -o|--offline)
                opts+=("$1")
                shift 1;;
            -rf|--resume-from)
                opts+=("$1" "$2")
                shift 2;;
            -U|--update-snapshots)
                opts+=("$1")
                shift 1;;
            *)
                break;;
        esac
    done

    local mod
    for mod in "$@"; do
        (
            m2chdir "$mod"
            coolmvn "$cmd" "${opts[@]}"
        )
    done
}

alias mpk='mvnlist package'
alias mte='mvnlist test'
alias min='mvnlist install'
alias mdp='mvnlist deploy'

alias MPK="mvnlist package -fn ${MAVENOPTS_SKIPTEST[*]}"
alias MIN="mvnlist install -fn ${MAVENOPTS_SKIPTEST[*]}"
alias MDP="mvnlist deploy  -fn ${MAVENOPTS_SKIPTEST[*]}"

alias M+='m2release'
alias 'mr-'='cd.x mvn release:prepare'
alias mr+='cd.x mvn release:perform'
alias mr0='cd.x mvn release:rollback'

alias M='m2chdir'

function m2chdir() {
    local path
    while read path; do
        cd "$path"
        return
    done < <(m2which -l "$@")
    echo "Not found: $@"
}
