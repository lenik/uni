include libauto/project.am

dist_bin_SCRIPTS = \
    cargo-ex \
    cargo-build

dist_man_MANS = \
    cargo-ex.1 \
    cargo-build.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}


install-debug:
	sudo ln -snf $$PWD/cargo-ex.in /usr/bin/cargo-ex
	sudo ln -snf $$PWD/cargo-build.in /usr/bin/cargo-build

