include libauto/coolauto.am

svnrootdir = /repos/svn
sitesdir   = ${sysconfdir}/apache2/sites-available
viewdir    = ${datadir}/repos-styles/repos-web/view

svnroot_DATA =

sites_DATA = \
    bodz-svn.site

dist_view_DATA = \
    z-repos.xsl

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
    prerm.in \
    bodz-svn.site.in

CLEANFILES = ${setup_SCRIPTS} \
    bodz-svn.site

