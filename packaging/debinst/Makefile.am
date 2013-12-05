include libauto/project.am

dist_bin_SCRIPTS = \
    debinst \
    .debinst.img

.%.img: %.i
	ai -ct bridge "$<" >$@

dist_pkgdata_SCRIPTS = \
    startup.sh

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
