include libauto/coolauto.am

sited2dir = /usr/include/d2/site
# zdogdir = ${sited2dir}/net/bodz/dog
lib32dir = ${libdir}/i386-linux-gnu
lib64dir = ${libdir}/x86_64-linux-gnu

lib32_DATA = \
    i386/libdog.a

lib64_DATA = \
    x86_64/libdog.a

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

# libdog.so.$(PACKAGE_VERSION):
i386/libdog.a:
	mkdir -p i386
	cd src; dmd -lib -m32 -of../$@ -op -Hd../i386/include/ `find -name '*.d'`

x86_64/libdog.a:
	mkdir -p x86_64
	cd src; dmd -lib -m64 -of../$@ -op -Hd../x86_64/include/ `find -name '*.d'`

install-data-hook:
	mkdir -p $(DESTDIR)$(sited2dir)
	rsync -av i386/include/ $(DESTDIR)$(sited2dir)

clean:
	rm -fr i386 x86_64
