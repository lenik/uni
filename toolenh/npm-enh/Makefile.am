include libauto/project.am

alias_DATA = \
    npmalias.sh

dist_bin_SCRIPTS = \
    npm-publish

dist_man_MANS = \
    npm-publish.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-debug:
	sudo ln -snf $$PWD/npm-publish.in $(DESTDIR)${bindir}/npm-publish

