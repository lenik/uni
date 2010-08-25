
templatesdir = ${pkgdatadir}/templates

dist_templates_DATA = \
    bash.sh

install-data-hook:
	shopt -s nullglob; \
	for f in $(DESTDIR)$(templatesdir)/*; do \
        base="$${f##*/}"; \
        chmod --reference $(srcdir)/$$base $$f; \
    done
