include libauto/project.am

dist_bin_SCRIPTS = \
    java/2print \
    java/criterion \
    java/daogen \
    java/foob \
    java/ls-entity \
    java/tcoll \
    java/tstool

dist_man_MANS =

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
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
	sudo ln -snf $$PWD/java/2print.in /bin/2print
	sudo ln -snf $$PWD/java/criterion.in /bin/criterion
	sudo ln -snf $$PWD/java/daogen.in /bin/daogen
	sudo ln -snf $$PWD/java/foob.in /bin/foob
	sudo ln -snf $$PWD/java/ls-entity.in /bin/ls-entity
	sudo ln -snf $$PWD/java/tcoll.in /bin/tcoll
	sudo ln -snf $$PWD/java/tstool.in /bin/tstool

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

