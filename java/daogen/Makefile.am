include libauto/project.am

dist_bin_SCRIPTS = \
    daogen

dist_man_MANS = 

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}

lib-updater:
	make -f m2proj.mf $@

lib-updater-O:
	make -f m2proj.mf $@

SOURCES = $(shell find src/main -type f)

test:
	echoli $(SOURCES)

target/: $(SOURCES)
	mvn package -fn -Dmaven.test.skip=true -DskipTests
	touch $@

install-data-hook:
	mkdir -p $(DESTDIR)$(pkgdatadir)/target
	cp classpath.release.lst $(DESTDIR)$(pkgdatadir)
	cp -RL target/classes $(DESTDIR)$(pkgdatadir)/target
	cp -RL lib $(DESTDIR)$(pkgdatadir)

install-debug:
	sudo ln -snf $$PWD/daogen.in /bin/daogen

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

open-tabs: tab-autobuild tab-libsweetjni-ab

tab-play:
	@kitty @set-tab-title 'daogen/play'
	@bash

tab-autobuild:
	kitty @launch --type tab --keep-focus --no-response \
            --tab-title "daogen/autobuild" \
            --cwd "$$PWD" \
            make autobuild

