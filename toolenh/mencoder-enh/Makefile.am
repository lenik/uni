include libauto/project.am

presetdir = ${pkgdatadir}/preset.d

do_subst += \
    -e 's,@presetdir@,${presetdir},g'

dist_bin_SCRIPTS = \
    aviremix \
    mencoder-preset

dist_man_MANS = \
    aviremix.1 \
    mencoder-preset.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${presetdir}
	cp -R ${srcdir}/preset.d/* ${DESTDIR}${presetdir}
