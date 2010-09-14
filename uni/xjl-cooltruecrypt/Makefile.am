include libauto/coolauto.am

SUBDIRS = test

sbindir = ${prefix}/sbin
sysconfdir = /etc
initdir = ${sysconfdir}/init
setupdir = ${datadir}/setup/${PACKAGE_NAME}

do_subst2 = ${do_subst} \
		-e 's,[@]sbindir[@],$(sbindir),g' \
        -e 's,[@]sysconfdir[@],$(sysconfdir),g' \
        -e 's,[@]initdir[@],$(initdir),g'

dist_bin_SCRIPTS = \
    nextdevid

sbin_SCRIPTS = \
	mount.tc \
    mountall.tc \
	fstab-install.tc \
	passwd-install.tc

dist_man_MANS = \
    mount.tc.1 \
    fstab.tc.5 \
    passwd.tc.5 \
    nextdevid.1

# sysconf_DATA =
pkgdata_DATA = \
    fstab.tc \
    passwd.tc

init_DATA = \
    mount-tc.conf

setup_SCRIPTS = \
	truecrypt-setup-x86 \
	post-install.sh \
	pre-remove.sh

EXTRA_DIST = \
	test.sh \
	mount.tc.in \
	mountall.tc.in \
	fstab-install.tc.in \
	passwd-install.tc.in \
	post-install.sh.in \
	pre-remove.sh.in

CLEANFILES = \
	mount.tc \
	mountall.tc \
	fstab-install.tc \
	passwd-install.tc \
	post-install.sh \
	pre-remove.sh \
	truecrypt-setup-x86

truecrypt-setup-x86:
	$(srcdir)/install-truecrypt.sh $@

%: %.in
	$(do_subst2) <$< >$@
