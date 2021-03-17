include libauto/project.am

archbits = $(shell uname -m | grep -q 64 && echo 64 || echo 32)

sited2dir = /usr/include/d2/site

lib_LIBRARIES = \
    libbas-d.a

dist_libbas_d_a_SOURCES = ${d_sources}

d_sources = \
    lenik/bas/esc/csisgr.d \
    lenik/bas/file.d \
    lenik/bas/log.d \
    lenik/bas/reflect.d \
    lenik/bas/str.d \
    lenik/bas/util/versions.d
d_objs = $(patsubst %.d, %.o, $(d_sources))

.PHONY: libbas-d.a.dmd

libbas-d.a: ${d_objs}
	ar rcs $@ $^
#	# r: insert/replace, c: create the ar, s: add an index

libbas-d.a.dmd:
	dmd -lib -m${archbits} -of$@ -op -Hdinclude -Dddoc ${dist_libbas_d_a_SOURCES}
#	-of=output-file
#	-op: preserve source path for output files
#	-Dd<dir> write doc file to dir

%.o: %.d ${dist_libbas_d_a_SOURCES}
	gdc -c -static -o $@ \
            -H -Hd include/$(dir $@) \
            -fdoc -fdoc-dir=doc/$(dir $@) \
            $<

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS} \
    libbas-d.o \
    libbas-d.a \
    libbas-d.a.dmd \
    ${d_objs}

install-data-hook:
	mkdir -p $(DESTDIR)$(sited2dir)
	rsync -av include/ $(DESTDIR)$(sited2dir)
	mkdir -p $(DESTDIR)$(docdir)
	rsync -av doc/ $(DESTDIR)$(docdir)

