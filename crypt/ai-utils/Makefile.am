include libauto/coolauto.am

AM_CPPFLAGS = ${GLIB_CFLAGS}

binfmtsdir = ${datadir}/binfmts
templatedir = ${pkgdatadir}/template

do_subst += \
    -e 's,[@]templatedir[@],${templatedir},g'

bin_PROGRAMS = \
    aiai

dist_aiai_SOURCES = aiai.c
aiai_LDADD = ${GLIB_LIBS} -lbas

dist_bashcmpl_DATA = \
    aiai.sh

dist_bin_SCRIPTS = \
    ai-runner

dist_binfmts_DATA = \
    aiscript

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

install-data-hook:
	cd ${DESTDIR}${bindir}; ln -snf aiai ai; ln -snf aiai unai
	mkdir -p ${DESTDIR}${templatedir}
	cp -R ${srcdir}/template/* ${DESTDIR}${templatedir}
