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

do_subst2 = $(do_subst2) -e 's,[@]bindconfdir[@],$(bindconfdir),g'

%: %.in Makefile
	$(do_subst2) <$< >$@
	chmod -w "$@"
