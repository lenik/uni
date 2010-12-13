include libauto/coolauto.am

svnrootdir = /repos/svn
sitesdir   = ${sysconfdir}/apache2/sites-available

svnroot_DATA =

dist_sites_DATA = \
    bodz-svn.site

install-data-hook:
	cd $(DESTDIR)$(svnrootdir); \
        for a in blobfiles bodz jazz sites times usnap; do \
            ln -snf /mnt/bodzdev/svn/$$a .; \
        done

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
	chmod -w "$@"
