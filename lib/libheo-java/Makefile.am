include libauto/project.am

dist_bin_SCRIPTS = \
    2heo \
    heo2

dist_man_MANS = \
    2heo.1 \
    heo2.1

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
	sudo ln -snf $$PWD/2heo.in /bin/2heo
	sudo ln -snf $$PWD/heo2.in /bin/heo2

