
function ollama() {
    local cmd="$1"
    local OLLAMA=$( which ollama )
    case "$cmd" in
        list|ls)
            shift
            ollama-list "$@"
            ;;
        *)
            $OLLAMA "$@";;
    esac
}

