include libauto/project.am

AI_TEMPLATE = bridge

dist_bin_SCRIPTS = \
    debinst

pkgdata_DATA = \
    debinst.img

dist_pkgdata_SCRIPTS = \
    install-debs.sh \
    install-rpms.tmpl

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
