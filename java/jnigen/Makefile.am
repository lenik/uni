# 

include libauto/project.am

dist_bin_SCRIPTS = \
    javap2h \
    jnigen

dist_man_MANS = \
    javap2h.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    $(dist_bin_SCRIPTS} \
    $(dist_man_MANS} \
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

