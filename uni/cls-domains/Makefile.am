include libauto/coolauto.am

SUBDIRS = lenik

bindconfdir = ${sysconfdir}/bind

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

%: %.in Makefile
	$(do_subst) \
        -e 's,[@]bindconfdir[@],$(bindconfdir),g' \
        <$< >$@

