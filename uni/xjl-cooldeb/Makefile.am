
do_subst = sed \
    -e 's,[@]prefix[@],$(prefix),g' \
    -e 's,[@]bindir[@],$(bindir),g' \
    -e 's,[@]docdir[@],$(docdir),g' \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]pkgdatadir[@],$(pkgdatadir),g'

dist_bin_SCRIPTS = \
    dh_make_ver

aliasesdir = /etc/bash_aliases.d

dist_aliases_DATA = \
    cooldeb.sh
