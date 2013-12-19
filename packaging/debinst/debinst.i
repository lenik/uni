# vim: set ft=sh :

declare -g -A deplevel

function main() {
    _dir_="${0%/*}"
    [ "$_dir_" = "$0" ] && _dir_=.

    if [ $# = 0 ]; then
        quit "No Debian package has been specified."
    fi
    local pkg1="$1"

    while read key val _; do
        case "$key" in
        "Version:")
            pkg1ver="$val";;
        "Architecture:")
            pkg1arch="$val";;
        esac
    done < <(apt-cache show "$pkg1")
    [ -z "$pkg1ver" ] && quit "Package version is unknown: $pkg1"
    [ -z "$pkg1arch" ] && quit "Package architecture is unknown: $pkg1"

    workdir=`mktemp -d`
    workdir_deb="$workdir/deb"
    workdir_rpm="$workdir/rpm"
    mkdir "$workdir_deb" "$workdir_rpm"

    _log2 "Copy the installer startup script."
        [ -d "$pkgdatadir" ] || pkgdatadir="$_dir_"
        cp -f "$pkgdatadir/install-debs.sh" "$workdir_deb"
        cp -f "$pkgdatadir/install-rpms.tmpl" "$workdir_rpm"

    _log1 "Download all individual .debs."
        cd "$workdir_deb"

        local pkg
        for pkg in "$@"; do
            download_debs "$pkg" '' 0
        done

        deporder=( $(
            for k in "${!deplevel[@]}"; do
                v=${deplevel[$k]}
                echo "$v:$k"
            done | sort -r -t: -k1,1 | cut -d: -f2
        ) )
        _log2 "Dependency-Order: ${deporder[@]}"

    _log1 "Convert $workdir_deb to self-extracting deb archive"
        archive="$userdir/$pkg1-$pkg1ver-$pkg1arch-$opt_deb_word-installer.run"

        makeself "$workdir_deb" "$archive" "$pkg1 installer (deb)" \
            ./install-debs.sh

        _log1 "Created $archive"

    _log1 "Convert $workdir_rpm to self-extracting rpm archive"
        archive="$userdir/$pkg1-$pkg1ver-$pkg1arch-$opt_rpm_word-installer.run"

        _log2 "Convert debs to rpms using alien"
        fakeroot alien -r -c *.deb ||
            quit "Failed to convert deb packages to rpms."

        _log2 "Move converted rpms to $workdir_rpm"
        mv *.rpm "$workdir_rpm" ||
            quit "Failed to move rpms to rpm workdir."

        cd "$workdir_rpm"

        sed -e "s/[@]PACKAGE_LIST[@]/${deporder[*]}/g" \
            <./install-rpms.tmpl \
            >./install-rpms.sh
        rm -f ./install-rpms.tmpl
        chmod +x ./install-rpms.sh

        makeself "$workdir_rpm" "$archive" "$pkg1 installer (rpm)" \
            ./install-rpms.sh

        _log1 "Created $archive"
}

function download_debs() {
    local pkg="$1"
    local uri_match="$2"
    local level="$3"
    local stack="$4"
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

    old_level=${deplevel[$pkg]}
    if [ -z "$old_level" ]; then
        deplevel[$pkg]=$level
    elif [ $level -gt "$old_level" ]; then
        deplevel[$pkg]=$level
    fi

    local key val
    local pkg_version
    local deps=()
    local ndep=0
    local list=()
    local dep

    while read key val; do
        [ -z "$key" ] && break          # only parse the first section.
        key="${key%\:}"
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

    ((level++))
    for dep in "${deps[@]}"; do
        if [ -z "${downloaded[$dep]}" ]; then
            download_debs "$dep" "$uri" "$level" "$stack$pkg"
        fi
    done
}
