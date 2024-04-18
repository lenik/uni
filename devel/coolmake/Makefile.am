include libauto/project.am

incdir = ${includedir}
samplesdir = ${docdir}/samples

do_subst += \
    -e 's,@incdir@,${incdir},g' \
    -e 's,@samplesdir@,${samplesdir},g'

dist_bin_SCRIPTS = \
    amexpand \
    autobuild \
    autorun \
    itreepdf \
    make-color \
    maketest \
    maketestvar \
    makelook \
    makeorig \
    subdirs

#dist_samples_DATA = \
#    samples/Makefile

dist_alias_DATA = \
    coolmake.sh

dist_man_MANS = \
    amexpand.1 \
    autobuild.1 \
    autorun.1 \
    makelook.1 \
    makeorig.1

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS}

install-data-hook:
	mkdir -p ${DESTDIR}${incdir}
	cp -a inc/ ${DESTDIR}${incdir}/coolmake
	mkdir -p ${DESTDIR}${pkgdatadir}
	cp -a template/ ${DESTDIR}${pkgdatadir}

install-debug:
	sudo mkdir -p ${DESTDIR}${incdir}/coolmake/
	sudo ln -snf $$PWD/inc/* ${DESTDIR}${incdir}/coolmake/
	sudo mkdir -p ${DESTDIR}${pkgdatadir}
	sudo ln -snf $$PWD/template/ ${DESTDIR}${pkgdatadir}

