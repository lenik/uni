include libauto/project.am

javalib_DATA = \
    lib/derby-*.jar

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"

install-data-hook:
	cd $(DESTDIR)$(javalibdir) && ln -snf derby-*.jar derby.jar
