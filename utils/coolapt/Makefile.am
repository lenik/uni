include libauto/coolauto.am

aptconfdir=${sysconfdir}/apt/apt.conf.d

dist_bin_SCRIPTS = \
    apt-fast \
    dwhich \
    lsdeb \
    debsize

dist_sbin_SCRIPTS = \
    apt-get-reinstall2

dist_man_MANS = \
    dwhich.1

dist_aliases_DATA = \
    aptutils.sh

dist_bashcmpl_DATA = \
    aptutils_compl.sh

dist_aptconf_DATA = \
    10allowunauth

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
