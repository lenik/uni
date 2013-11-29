function main() {
    _dir_="${0%/*}"
    [ "$_dir_" = "$0" ] && _dir_=.

    if [ $# = 0 ]; then
        quit "No Debian package has been specified."
    fi
    local pkg1="$1"

    workdir=`mktemp -d`

    _log2 "Copy the installer startup script."
        startup_f="$pkgdatadir/startup.sh"
        test -x "$startup_f" || startup_f="$_dir_/startup.sh"
        test -x "$startup_f" || quit "The startup script isn't available."
        cp -f "$startup_f" "$workdir"

    cd "$workdir"

    # Download all individual .debs.
        local pkg
        for pkg in "$@"; do
            download_debs "$pkg"
        done

    _log2 "Convert $workdir to self-extracting archive $archive."
        archive="$userdir/$pkg1-installer.run"
        makeself "$workdir" "$archive" "$pkg1 installer" ./startup.sh

        _log1 "The creation of $pkg installer is completed."
        _log1 "See: $archive"
}

function download_debs() {
    local pkg="$1"
    local uri_match="$2"
    local stack="$3"
    local priority uri dist
    local c1 c2 c3

    # which source ..
    # "500 http://dlocal/                 unstable/    Packages"
    # "500 http://mirrors.163.com/debian/ squeeze/main i386 Packages"

    while read priority uri dist c1 c2 c3 _; do
        # we only need the first rule whose should be high priority..
        break
    done < <( apt-cache policy "$pkg" | grep :// )

    # We are only interesting in the uri.
    if [ -z "$uri" ]; then
        _error "Package seems not available: $pkg" >&2
        return 1
    fi

    if [ -n "$uri_match" ]; then
        if [ "$uri_match" != "$uri" ]; then
            _log2 "Ignored package outside of the origin source: $pkg"
            return 0
        fi
    fi

    _log2 "$stack: Download $pkg into $PWD."
    apt-get -q download "$pkg" \
        || quit "Failed to download the package $pkg"

    downloaded[$pkg]=1

    local key val
    local pkg_version
    local deps=()
    local ndep=0
    local list=()
    local dep

    while read key val; do
        [ -z "$key" ] && break          # only parse the first section.
        key="${key%:}"
        case "$key" in
            Version)
                pkg_version="$val";;
            Depends|Recommends)
                IFS=',|' read -a list < <(echo "$val")
                for dep in "${list[@]}"; do
                    dep="${dep# }"
                    dep="${dep%%(*}"
                    dep="${dep% }"
                    deps[ndep++]="$dep"
                done
                ;;
        esac
    done < <( apt-cache show "$pkg" )

    _log2 "Candidate dependencies: ${deps[@]}"
    [ -n "$stack" ] && stack="$stack -> "

    for dep in "${deps[@]}"; do
        if [ -z "${downloaded[$dep]}" ]; then
            download_debs "$dep" "$uri" "$stack$pkg"
        fi
    done
}
