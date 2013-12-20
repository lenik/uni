include libauto/project.am

AM_CPPFLAGS = ${GLIB_CFLAGS}

binfmtsdir = ${datadir}/binfmts
templatedir = ${pkgdatadir}/template

do_subst += \
    -e 's,[@]templatedir[@],${templatedir},g'

bin_PROGRAMS = \
    ai \
    unai

dist_ai_SOURCES = ai.c
ai_LDADD = ${GLIB_LIBS} -lbas

dist_unai_SOURCES = unai.c
unai_LDADD = ${GLIB_LIBS} -lbas

dist_completion_DATA = \
    aiai.sh

dist_bin_SCRIPTS = \
    runai

dist_man_MANS = \
    ai.1 \
    unai.1

#dist_sysvinit_SCRIPTS =
dist_pkgdata_SCRIPTS = \
    aifmt

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${templatedir}
	cp -R ${srcdir}/template/* ${DESTDIR}${templatedir}
