include libauto/project.am

MAKE2=MAKEFLAGS= MAKEOVERRIDES= make MAKELEVEL=0

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	cd ../crosstool-ng.orig && ./bootstrap
	cd ../crosstool-ng.orig && ./configure --prefix=${prefix}
	cd ../crosstool-ng.orig && $(MAKE2)
	cd ../crosstool-ng.orig && $(MAKE2) install DESTDIR="$(DESTDIR)"
