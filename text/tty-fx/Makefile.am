include libauto/project.am

dist_bin_SCRIPTS = \
    ansifilter \
    ansifx \
    colorlog \
    meter \
    settermtitle \
    sgr256 \
    xtermtitle

dist_alias_DATA = \
    stty-aliases.sh

dist_man_MANS = \
    ansifilter.1 \
    ansifx.1 \
    colorlog.1 \
    sgr256.1 \
    xtermtitle.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    get-ansifilter

CLEANFILES = \
    ansifilter \
    ansifilter.1 \
    ${dist_setup_SCRIPTS}

ansifilter ansifilter.1: get-ansifilter Makefile
	$(srcdir)/get-ansifilter $@
