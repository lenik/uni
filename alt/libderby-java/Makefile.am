include libauto/project.am

javalib_DATA = \
    lib/derby-*.jar

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(javalibdir) && ln -snf derby-*.jar derby.jar
