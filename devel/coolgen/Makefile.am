include libauto/project.am

SUBDIRS = lib

dist_bin_SCRIPTS = \
    new \
    rnd

dist_man_MANS = \
    new.1

newtmpl_DATA =

CLEANFILES = ${dist_bin_SCRIPTS}

install-data-hook:
	mkdir -p ${DESTDIR}${newtmpldir}
	cp -R ${srcdir}/newtmpl.d/* ${DESTDIR}${newtmpldir}
	cd $(DESTDIR)${bindir}; \
		for tmpl in $(DESTDIR)${newtmpldir}/*; do \
			base="$${tmpl##*/}"; \
			base="$${base%.*}"; \
			ln -snf new new-$$base; \
		done

install-debug:
	ln -snf $$PWD/${srcdir}/new.in ${DESTDIR}${bindir}/new
	mkdir -p ${DESTDIR}${newtmpldir}
	rm -dr ${DESTDIR}${newtmpldir}/cocos
	ln -snf $$PWD/${srcdir}/newtmpl.d/* ${DESTDIR}${newtmpldir}
	cd $(DESTDIR)${bindir}; \
		for tmpl in $(DESTDIR)${newtmpldir}/*; do \
			base="$${tmpl##*/}"; \
			base="$${base%.*}"; \
			ln -snf new new-$$base; \
		done
