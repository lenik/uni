include libauto/project.am

dist_bin_SCRIPTS = \
    java/typedet \

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
	sudo ln -snf $$PWD/java/typedet.in /bin/typedet

autobuild:
	@echo "autobuild isn't supported, yet."
	@echo "try install-debug instead."
	@echo "see also: autoinstall"
	@read

open-window:
	kitty -T "supertask:typedet" \
	        -o allow_remote_control=yes \
	        -o confirm_os_window_close=0 \
            make open-tabs tab-play

open-tabs: tab-autobuild

tab-play:
	@kitty @set-tab-title 'typedet/play'
	@bash

tab-autobuild:
	kitty @launch --type tab --keep-focus --no-response \
            --tab-title "typedet/autobuild" \
            --cwd "$$PWD" \
            make autobuild

