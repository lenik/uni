include libauto/project.am

SUBDIRS = newtmpl.d

dist_bin_SCRIPTS = \
    new \
    rnd

dist_man_MANS = \
    new.1

newtmpl_DATA =

CLEANFILES = ${dist_bin_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)$(bindir); \
		for tmpl in $(DESTDIR)$(newtmpldir)/*; do \
			base="$${tmpl##*/}"; \
			base="$${base%.*}"; \
			ln -snf new new-$$base; \
		done
