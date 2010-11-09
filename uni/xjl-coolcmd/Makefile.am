include libauto/coolauto.am

cmtlibdir = ${perllibdir}/cmt

dist_aliases_SCRIPTS = \
    coolcmd.sh

dist_cmtlib_DATA = \
    cmt/subst.pm

dist_bin_SCRIPTS = \
    cd-exec \
    subst \
    substecho \
    substexec \
    substcat \
    rsubstcat \
    rsubstecho \
    rsexec \
    rsexec-c \
    ifrsubst \
    substtree \
    echox \
    swapox \
    testox \
    colorox \
    loop \
    localhost \
    postproc \
    fnotify \
    onmodify \
    makeoutdated

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
