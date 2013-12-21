include libauto/project.am

cmtlibdir = ${perllibdir}/cmt

dist_aliases_SCRIPTS = \
    coolcmd.sh

dist_cmtlib_DATA = \
    cmt/subst.pm

bin_PROGRAMS = \
    echo-x \
    xcat

AM_CPPFLAGS = $(GLIB_CFLAGS)
xcat_LDADD = $(GLIB_LIBS)

dist_bin_SCRIPTS = \
    cd-exec \
    colorox \
    eachdir \
    esti-exec \
    ifrsubst \
    ldd-which \
    localhost \
    loop \
    makeoutdated \
    make1 \
    onchange \
    postproc \
    repeat \
    run-bg \
    rsexec \
    rsexec-c \
    rsubstcat \
    rsubstecho \
    subst \
    substcat \
    substecho \
    substexec \
    substtree \
    swapox \
    testox \
    until-changed \
    xxcat \
    xxxcat \
    xxxxcat \
    xless \
    xxless \
    xxxless \
    xxxxless \
    xmore_ \
    xxmore \
    xxxmore \
    xxxxmore

dist_man_MANS = \
    eachdir.1 \
    esti-exec.1 \
    ldd-which.1 \
    localhost.1 \
    make1.1 \
    onchange.1 \
    repeat.1 \
    subst.1 \
    until-changed.1 \
    xcat.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}
