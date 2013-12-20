include libauto/project.am

do_subst += \
    -e 's,[@]dconfdir[@],$(dconfdir),g' \
    -e 's,[@]dcprofdir[@],$(dcprofdir),g' \
    -e 's,[@]sitedbdir[@],$(sitedbdir),g'

dconfdir    = ${sysconfdir}/dconf
dcprofdir   = ${dconfdir}/profile
sitedbdir = ${dconfdir}/db/site.d

dist_bin_SCRIPTS = \
    mate-switcher

dist_dcprof_DATA = \
    profile/user

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

install-data-hook:
	mkdir -p $(DESTDIR)$(sitedbdir); \
        rsync -amv "${srcdir}/site.d/" "${DESTDIR}${sitedbdir}"
