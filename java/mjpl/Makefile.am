include libauto/project.am

bin_PROGRAMS = \
    mjpl

dist_mjpl_SOURCES = \
    mjpl.d \
    lenik/uni/mjpl/m2env.d \
    lenik/uni/mjpl/pom.d

mjpl: ${dist_mjpl_SOURCES}
	gdc -O3 -frelease \
        -I/usr/include/d2/site \
        -fignore-unknown-pragmas \
        -o $@ \
        ${dist_mjpl_SOURCES} \
        -lbas-d

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${setup_SCRIPTS}
