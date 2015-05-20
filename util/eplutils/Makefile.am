include libauto/project.am

do_subst += \
	-e 's,@extdir@,${extdir},g'

extdir=${pkgdatadir}

dist_bin_SCRIPTS = \
	gwimage

dist_man_MANS = \
	gwimage.1

dist_ext_DATA = \
	xpm2gw.c

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
