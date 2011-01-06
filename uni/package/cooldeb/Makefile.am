include libauto/coolauto.am

do_subst += -e 's,[@]debdefldir[@],$(debdefldir),g'

debdefldir = ${pkgdatadir}/debian-defaults

dist_bin_SCRIPTS = \
    autofill-control \
    cideb \
    deb-clean \
    dh_check_depends \
    dh_debname \
    dh_incr \
    dh_make-autover \
    dh_package_field \
    dir-ver \
    newdeb \
    rebuild \
    rebuilds \
    verexec \
    vicontrol

bin_SCRIPTS = \
    dh_add_missing

dist_aliases_DATA = \
    cooldeb.sh

dist_bashcmpl_DATA = \
    cooldeb

dist_man_MANS = \
    newdeb.1 \
    cideb.1

dist_pkgdata_DATA = \
    postinst.def \
    prerm.def

debdefl_DATA =

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    dh_add_missing.in \
    debian-defaults

CLEANFILES = \
    ${setup_SCRIPTS} \
    dh_add_missing

dist-hook:
	find $(distdir)/debian-defaults -type d -name .svn -exec rm -fR {} \; -prune

vcmdlist = dh_make pwd

install-data-hook:
	cp -RT $(srcdir)/debian-defaults $(DESTDIR)$(debdefldir)
	find $(DESTDIR)$(debdefldir) -type d -name .svn -exec rm -fR {} \; -prune

	cd $(DESTDIR)$(bindir); \
            for f in $(vcmdlist); do \
                ln -sf verexec $${f}_ver; \
            done; \
            ln -sf dir-ver dir-nover

uninstall-hook:
	cd $(DESTDIR)$(bindir); \
            for f in $(vcmdlist); do \
                rm -f \$${f}_ver; \
            done; \
            rm -f dir-nover
