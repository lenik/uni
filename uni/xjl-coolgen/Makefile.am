include libauto/coolauto.am

SUBDIRS = newtmpl.d

bin_SCRIPTS = \
    new

dist_man_MANS = \
    new.1

newtmpl_DATA =

EXTRA_DIST = \
    new.in

CLEANFILES = \
    new

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		for tmpl in $(DESTDIR)$(newtmpldir)/*; do \
			base="$${tmpl##*/}"; \
			base="$${base%.*}"; \
			ln -snf new new-$$base; \
		done

