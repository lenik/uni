
do_subst = sed \
    -e 's/master-file/generated-file, please do not edit/' \
    -e 's,[@]prefix[@],$(prefix),g' \
    -e 's,[@]bindir[@],$(bindir),g' \
    -e 's,[@]docdir[@],$(docdir),g' \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]pkgdatadir[@],$(pkgdatadir),g'

dist_bin_SCRIPTS = \
    mount.tc \
    mountall.tc \
    nextdevid

man_MANS = \
    mount.tc.1 \
    fstab.tc.5 \
    passwd.tc.5 \
    nextdevid.1

sysconfdir = /etc
upstartconfdir = ${sysconfdir}/init

# permission??
sysconf_DATA = \
    fstab.tc \
    passwd.tc

upstartconf_DATA = \
    mount-tc.conf
