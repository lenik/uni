
include libauto/subst.am

dist_bin_SCRIPTS = \
    generate

dist_man_MANS = \
    generate.1

install-data-hook:
	for t in $(srcdir)/templates/*; do \
        tnam=${t##*/}; \
        tnam=${t%%.*}; \
        cd $(bindir); ln -sf generate gen-\$tnam; \
        cd $(man1dir); ln -sf generate.1 gen-\$tnam.1; \
    done

uninstall-hook:
	for t in $(srcdir)/templates/*; do \
        tnam=${t##*/}; \
        tnam=${t%%.*}; \
        cd $(bindir); rm -f gen-\$tnam; \
        cd $(man1dir); rm -f gen-\$tnam.1; \
    done
