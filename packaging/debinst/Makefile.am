include libauto/project.am

AI_TEMPLATE = bridge

dist_bin_SCRIPTS = \
    debinst

pkgdata_DATA = \
    debinst.img

dist_pkgdata_SCRIPTS = \
    install-debs.sh \
    install-rpms.tmpl

dist_man_MANS = \
    debinst.8

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    debinst.img \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
