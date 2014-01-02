include libauto/project.am

aptconfdir=${sysconfdir}/apt/apt.conf.d

dist_bin_SCRIPTS = \
    apt-f \
    apt-fast \
    apt-list-dep \
    dwhich \
    lsdeb \
    debsize

dist_sbin_SCRIPTS = \
    apt-get-reinstall2

dist_man_MANS = \
    apt-f.8 \
    debsize.8 \
    dwhich.8 \
    lsdeb.8

dwhich.8:

dist_alias_DATA = \
    aptutils.sh

dist_completion_DATA = \
    aptutils_compl.sh

dist_aptconf_DATA = \
    10allowunauth

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
