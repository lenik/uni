include libauto/project.am

archbits = $(shell uname -m | grep -q 64 && echo 64 || echo 32)

sited2dir = /usr/include/d2/site

lib_LIBRARIES = \
    libbas-d.a

.PHONY: libbas-d.a
libbas-d.a:
	cd src; dmd -lib -m${archbits} -of../$@ -op -Hd../include `find -name '*.d'`

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS} 

install-data-hook:
	mkdir -p $(DESTDIR)$(sited2dir)
	rsync -av include/ $(DESTDIR)$(sited2dir)

