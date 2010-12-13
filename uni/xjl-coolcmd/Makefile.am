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

dist_bin_SCRIPTS = \
    cd-exec \
    colorox \
    fnotify \
    ifrsubst \
    localhost \
    loop \
    makeoutdated \
    onmodify \
    postproc \
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
    testox

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
