include libauto/project.am

dist_bin_SCRIPTS = \
    bin/2print \
    bin/criterion \
    bin/daogen \
    bin/foob \
    bin/ls-entity \
    bin/tstool

dist_man_MANS = \
    bin/2print.1 \
    bin/criterion.1 \
    bin/daogen.1 \
    bin/foob.1 \
    bin/ls-entity.1 \
    bin/tstool.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}

SOURCES = $(shell find java/src/main -type f)

test:
	echoli $(SOURCES)

install-data-hook:
	mkdir -p $(DESTDIR)$(pkgdatadir)/target
	cp java/classpath.release.lst $(DESTDIR)$(pkgdatadir)
	cp -RL java/target/classes $(DESTDIR)$(pkgdatadir)/target
	cp -RL java/lib $(DESTDIR)$(pkgdatadir)

install-debug:
	sudo ln -snf $$PWD/bin/2print           /bin/2print
	sudo ln -snf $$PWD/bin/criterion        /bin/criterion
	sudo ln -snf $$PWD/bin/daogen           /bin/daogen
	sudo ln -snf $$PWD/bin/foob             /bin/foob
	sudo ln -snf $$PWD/bin/ls-entity        /bin/ls-entity
	sudo ln -snf $$PWD/bin/tstool           /bin/tstool

autobuild:
	@echo "autobuild isn't supported, yet."
	@echo "try install-debug instead."
	@echo "see also: autoinstall"
	@read

open-window:
	kitty -T "supertask:daogen" \
	        -o allow_remote_control=yes \
	        -o confirm_os_window_close=0 \
            make open-tabs tab-play

open-tabs: tab-autobuild

tab-play:
	@kitty @set-tab-title 'daogen/play'
	@bash

tab-autobuild:
	kitty @launch --type tab --keep-focus --no-response \
            --tab-title "daogen/autobuild" \
            --cwd "$$PWD" \
            make autobuild

