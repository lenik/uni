include libauto/coolauto.am

svnrootdir = /repos/svn
svnconfdir = ${svnrootdir}/conf
sitesdir   = ${sysconfdir}/apache2/sites-available
viewdir    = ${datadir}/repos-styles/repos-web/view
xinetdir   = ${sysconfdir}/xinetd.d

do_subst += \
    -e 's,[@]svnrootdir[@],${svnrootdir},g' \
    -e 's,[@]svnconfdir[@],${svnconfdir},g' \
    -e 's,[@]sitesdir[@],${sitesdir},g' \
    -e 's,[@]viewdir[@],${viewdir},g' \
    -e 's,[@]xinetdir[@],${xinetdir},g'

dist_svnconf_DATA = \
    svnserve.conf

sites_DATA = \
    bodz-svn.site

dist_view_DATA = \
    z-repos.xsl

dist_xinet_DATA = \
    svn

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
    svnserve.conf \
    bodz-svn.site \
    svn

