include libauto/coolauto.am
include libauto/allin.am

SUBDIRS = test

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
    postinst \
    prerm

EXTRA_DIST = \
    test.sh \
    mount.tc.in \
    mountall.tc.in \
    fstab-install.tc.in \
    passwd-install.tc.in \
    postinst.in \
    prerm.in

CLEANFILES = \
    mount.tc \
    mountall.tc \
    fstab-install.tc \
    passwd-install.tc \
    postinst \
    prerm \
    truecrypt-setup-x86

truecrypt-setup-x86:
	$(srcdir)/install-truecrypt.sh $@

