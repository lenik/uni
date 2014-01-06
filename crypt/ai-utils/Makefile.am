include libauto/project.am

AM_CPPFLAGS = ${GLIB_CFLAGS} -DLOG_LEVEL=4

binfmtsdir = ${datadir}/binfmts
templatedir = ${pkgdatadir}/template

do_subst += \
    -e 's,[@]templatedir[@],${templatedir},g'

bin_PROGRAMS = \
    ai \
    air \
    unai

dist_ai_SOURCES = ai.c aicodec.c
ai_LDADD = ${GLIB_LIBS} -lbas

dist_air_SOURCES = air.c aicodec.c
air_LDADD = ${GLIB_LIBS} -lbas

dist_unai_SOURCES = unai.c aicodec.c
unai_LDADD = ${GLIB_LIBS} -lbas

dist_completion_DATA = \
    completions/ai_utils

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
	cd ${DESTDIR}${completiondir}; ln -snf ai_utils ai; ln -snf ai_utils unai
