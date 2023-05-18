include libauto/project.am

dist_bin_SCRIPTS = \
    encoder/base45d \
    encoder/qrcopy

dist_man_MANS = 

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}

SOURCES = $(shell find encoder/src/main -type f)

test:
	echoli $(SOURCES)

install-data-hook:
	mkdir -p $(DESTDIR)$(pkgdatadir)/target
	cp encoder/classpath.release.lst $(DESTDIR)$(pkgdatadir)
	cp -RL encoder/target/classes $(DESTDIR)$(pkgdatadir)/target
	cp -RL encoder/lib $(DESTDIR)$(pkgdatadir)

install-debug:
	sudo ln -snf $$PWD/encoder/qrcopy.in /bin/qrcopy
	sudo ln -snf $$PWD/encoder/base45d.in /bin/base45d

autobuild:
	@echo "autobuild isn't supported, yet."
	@echo "try install-debug instead."
	@echo "see also: autoinstall"
	@read

open-window:
	kitty -T "supertask:qrcopy" \
	        -o allow_remote_control=yes \
	        -o confirm_os_window_close=0 \
            make open-tabs tab-play

open-tabs: tab-autobuild

tab-play:
	@kitty @set-tab-title 'qrcopy/play'
	@bash

tab-autobuild:
	kitty @launch --type tab --keep-focus --no-response \
            --tab-title "qrcopy/autobuild" \
            --cwd "$$PWD" \
            make autobuild

