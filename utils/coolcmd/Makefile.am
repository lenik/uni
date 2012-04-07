include libauto/coolauto.am

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

%.c: %.c.in
	$(do_subst) <$< >$@

dist_bin_SCRIPTS = \
    cd-exec \
    colorox \
    eachdir \
    ifrsubst \
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

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

