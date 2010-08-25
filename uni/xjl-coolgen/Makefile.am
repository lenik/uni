
include libauto/subst.am

templatesdir = ${pkgdatadir}/templates

SUBDIRS = templates

bin_SCRIPTS = \
    generate

dist_man_MANS = \
    generate.1

EXTRA_DIST = generate.in
CLEANFILES = generate

generate: generate.in
	$(do_subst) -e 's,[@]templatesdir[@],${templatesdir},g' \
		<$(srcdir)/generate.in >$@

install-data-hook:
	for t in $(srcdir)/templates/*; do \
        tnam=$${t##*/}; \
        tnam=$${tnam%.*}; \
		if [ "$$tnam" = "Makefile" ]; then continue; fi; \
        cd $(DESTDIR)$(bindir);  ln -sf generate gen-$$tnam; \
        cd $(DESTDIR)$(man1dir); ln -sf generate.1 gen-$$tnam.1; \
    done

uninstall-hook:
	for t in $(srcdir)/templates/*; do \
        tnam=$${t##*/}; \
        tnam=$${tnam%%.*}; \
		if [ "$$tnam" = "Makefile" ]; then continue; fi; \
        cd $(DESTDIR)$(bindir);  rm -f gen-$$tnam; \
        cd $(DESTDIR)$(man1dir); rm -f gen-$$tnam.1; \
    done
