include libauto/project.am

dcompiler = gdc \
    -O3 -frelease \
    -I/usr/include/d2/site \
    -fignore-unknown-pragmas

bin_PROGRAMS = \
    mjpl$(EXEEXT)

dist_mjpl_SOURCES = \
    mjpl.d \
    lenik/uni/mjpl/m2env.d \
    lenik/uni/mjpl/pom.d

mjpl$(EXEEXT): ${dist_mjpl_SOURCES}
	$(dcompiler) -o $@ ${dist_mjpl_SOURCES} -lbas-d

dist_man_MANS = \
    mjpl.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
