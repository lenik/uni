include libauto/project.am

sited2dir = /usr/include/d2/site

lib_LIBRARIES = \
    libdog.a

dist_libdog_d_a_SOURCES = ${d_sources}

d_sources = \
    net/bodz/dog/logging.d \
    net/bodz/dog/dprog.d \
    net/bodz/dog1.d

d_objs = $(patsubst %.d, %.o, $(d_sources))

libdog.a: ${d_objs}                                                           
	ar rcs $@ $^

%.o: %.d ${d_sources}
	gdc -c -o $@ \
	    -H -Hd include/$(dir $@) \
	    -fdoc -fdoc-dir=doc/$(dir $@) \
	    $<

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS} \
    libdog.o \
    libdog.a \
    ${d_objs}

install-data-hook:
	mkdir -p $(DESTDIR)$(sited2dir)
	rsync -av include/ $(DESTDIR)$(sited2dir)
	mkdir -p $(DESTDIR)$(docdir)
	rsync -av doc/ $(DESTDIR)$(docdir)

