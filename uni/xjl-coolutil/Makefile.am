
do_subst = sed \
    -e 's,[@]prefix[@],$(prefix),g' \
    -e 's,[@]bindir[@],$(bindir),g' \
    -e 's,[@]docdir[@],$(docdir),g' \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]pkgdatadir[@],$(pkgdatadir),g'

bin_PROGRAMS = \
    uc

dist_bin_SCRIPTS = \
    swap \
    which2 \
    whichother \
    mount-vex \
    umount-vex \
    mountall-alt \
    umount-alt-a \
    df-x \
    sedit \
    headrm \
    grepl

dist_man_MANS = \
    sedit.1

install-exec-hook:
	cd $(DESTDIR)$(bindir); \
    $(LN_S) -f uc lc; \
    $(LN_S) -f uc to-caption; \
    $(LN_S) -f uc to-underline; \
    $(LN_S) -f uc to-camel; \
    $(LN_S) -f uc to-mech

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
	rm -f lc to-caption to-underline to-camel to-mech
