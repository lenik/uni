
include libauto/coolauto.am

bindconfdir = ${sysconfdir}/bind

bindconf_DATA = \
    bind/cooldns.options

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    postinst \
    prerm

do_subst2 = $(do_subst) -e 's,[@]bindconfdir[@],$(bindconfdir),g'

%: %.in Makefile
	$(do_subst2) <$< >$@
	chmod -w "$@"
