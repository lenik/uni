# vim: set filetype=sh :

function Eoff() {
    stty -echo
}

function Eon() {
    stty echo
}

# ^+u269d
function ⚝() {
    local _stty=`stty -a`
    if [ "$_stty" = "${_stty/-echo }" ]; then
        # echo Disable echo mode.
        _PS1_ECHO="$PS1"
        PS1="⚝  "
        stty -echo
    else
        # echo Restore echo mode.
        PS1="$_PS1_ECHO"
        _PS1_ECHO=
        stty echo
    fi
}

alias title='settermtitle'

