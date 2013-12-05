include libauto/project.am

samplesdir = ${pkgdatadir}/samples

dist_bin_SCRIPTS = \
    rrdwrapper-index

dist_shlib_DATA = \
    shlib.d/rrdwrapper

dist_samples_SCRIPTS = \
    test/demo.rrw

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
