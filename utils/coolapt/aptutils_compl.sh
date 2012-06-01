
function _apt_packages_all() {
    local cur="${COMP_WORDS[COMP_CWORD]}"
    COMPREPLY=( $( apt-cache --no-generate pkgnames $cur) )
    return 0
}

function _apt_packages_installed() {
    local cur="${COMP_WORDS[COMP_CWORD]}"
    COMPREPLY=( $( _comp_dpkg_installed_packages "$cur" ) )
}

complete -F _apt_packages_all       as sagi safi acp
complete -F _apt_packages_installed sagu sagp sagr dtree sdr
