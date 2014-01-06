include libauto/project.am
include libauto/subst-ai.am

AI = ./ai
export AI_KEYFILE =
AI_TEMPLATE = template/bridge

binfmtsdir = ${datadir}/binfmts
templatedir = ${pkgdatadir}/template

do_subst += \
    -e 's,[@]templatedir[@],${templatedir},g'

bin_PROGRAMS = \
    ai \
    air \
    unai

AM_CPPFLAGS = ${GLIB_CFLAGS} -DLOG_LEVEL=4

dist_ai_SOURCES = ai.c aicodec.c
ai_LDADD = ${GLIB_LIBS} -lbas

dist_air_SOURCES = air.c aicodec.c
air_LDADD = ${GLIB_LIBS} -lbas

dist_unai_SOURCES = unai.c aicodec.c
unai_LDADD = ${GLIB_LIBS} -lbas

dist_completion_DATA = \
    completions/ai_utils

pkgdata_SCRIPTS = \
    loadimage

loadimage: loadimage.o aicodec.o
	ld -x -Bshareable -o $@ $^
loadimage.o: loadimage.c loadimage.c.in
	gcc -fpic -I/usr/include/bash -c -o $@ $< -lbas

#dist_sysvinit_SCRIPTS =
dist_pkgdata_SCRIPTS = \
    aifmt

dist_man_MANS = \
    ai.1 \
    air.1 \
    unai.1

dist_shlib_DATA = \
    shlib.d/ai

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${templatedir}
	cp -R ${srcdir}/template/* ${DESTDIR}${templatedir}
	cd ${DESTDIR}${completiondir}; \
            ln -snf ai_utils ai; \
            ln -snf ai_utils unai
	mkdir -p ${DESTDIR}${libdir}
	mv ${DESTDIR}${pkgdatadir}/loadimage ${DESTDIR}${libdir}
