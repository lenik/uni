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
        -fproperty \
        -o $@ \
        ${dist_mjpl_SOURCES} \
        -lbas-d

dist_man_MANS = \
    mjpl.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
