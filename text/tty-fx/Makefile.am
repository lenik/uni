include libauto/project.am

dist_bin_SCRIPTS = \
    ansifilter \
    ansifx \
    meter \
    settermtitle \
    sgr256 \
    colorlog

dist_aliases_DATA = \
    stty-aliases.sh

dist_man_MANS = \
    ansifilter.1

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
	$(srcdir)/$< $@
