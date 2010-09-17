
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

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]bindconfdir[@],$(bindconfdir),g' \
		<$< >$@
