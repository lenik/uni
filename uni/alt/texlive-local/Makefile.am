include libauto/coolauto.am

profilesdir = ${sysconfdir}/profile.d

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) \
            -e 's,[@]profilesdir[@],$(profilesdir),g' \
            <$< >$@
