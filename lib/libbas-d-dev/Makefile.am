include libauto/project.am

archbits = $(shell uname -m | grep -q 64 && echo 64 || echo 32)

sited2dir = /usr/include/d2/site

lib_LIBRARIES = \
    libbas-d.a

dist_libbas_d_a_SOURCES = \
    lenik/bas/esc/csisgr.d \
    lenik/bas/file.d \
    lenik/bas/log.d \
    lenik/bas/reflect.d \
    lenik/bas/str.d \
    lenik/bas/util/versions.d

.PHONY: libbas-d.a libbas-d.a.dmd
libbas-d.a: libbas-d.o libbas-d.a.dmd
	ar rcs $@ $<
libbas-d.a.dmd:
	dmd -lib -m${archbits} -of$@ -op -Hdinclude -Dddoc ${dist_libbas_d_a_SOURCES}

.PHONY: libbas-d.o
libbas-d.o:
	gdc -c -o $@ \
            -fintfc -fintfc-dir=include.tmp \
            -fdoc -fdoc-dir=doc.tmp \
            ${dist_libbas_d_a_SOURCES}

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS} \
    libbas-d.o \
    libbas-d.a \
    libbas-d.a.dmd

install-data-hook:
	mkdir -p $(DESTDIR)$(sited2dir)
	rsync -av include/ $(DESTDIR)$(sited2dir)
	mkdir -p $(DESTDIR)$(docdir)
	rsync -av doc/ $(DESTDIR)$(docdir)

