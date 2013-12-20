include libauto/project.am

SUBDIRS = test

dist_bin_SCRIPTS = \
    nextdevid

dist_sbin_SCRIPTS = \
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

dist_setup_SCRIPTS = \
    truecrypt-setup-x86 \
    postinst \
    prerm

truecrypt-setup-x86:
	$(srcdir)/install-truecrypt.sh $@

CLEANFILES = \
    ${dist_sbin_SCRIPTS} \
    ${dist_setup_SCRIPTS}
