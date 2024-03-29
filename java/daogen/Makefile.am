include libauto/project.am

dist_bin_SCRIPTS = \
    shell/cmd-runner \
    shell/2print \
    shell/criterion \
    shell/daogen \
    shell/foob \
    shell/ls-entity \
    shell/tcoll \
    shell/tstool

#dist_man_MANS = \
#    shell/2print.1 \
#    shell/criterion.1 \
#    shell/daogen.1 \
#    shell/foob.1 \
#    shell/ls-entity.1 \
#    shell/tcoll.1 \
#    shell/tstool.1

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
	sudo ln -snf $$PWD/shell/cmd-runner.in  /bin/cmd-runner
	sudo ln -snf $$PWD/shell/2print         /bin/2print
	sudo ln -snf $$PWD/shell/criterion      /bin/criterion
	sudo ln -snf $$PWD/shell/daogen         /bin/daogen
	sudo ln -snf $$PWD/shell/foob           /bin/foob
	sudo ln -snf $$PWD/shell/ls-entity      /bin/ls-entity
	sudo ln -snf $$PWD/shell/tcoll          /bin/tcoll
	sudo ln -snf $$PWD/shell/tstool         /bin/tstool

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

