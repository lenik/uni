
do_subst = sed \
    -e 's,[@]prefix[@],$(prefix),g' \
    -e 's,[@]bindir[@],$(bindir),g' \
    -e 's,[@]docdir[@],$(docdir),g' \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]pkgdatadir[@],$(pkgdatadir),g'

dist_bin_SCRIPTS = \
    swap \
    which2 \
    whichother \
    mount-vex \
    umount-vex \
    mountall-alt \
    umount-alt-a \
    df-x
