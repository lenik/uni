include libauto/coolauto.am

bindconfdir = ${sysconfdir}/bind

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
	bind \
	postinst.in \
	prerm.in

CLEANFILES = \
	postinst \
	prerm

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]bindconfdir[@],$(bindconfdir),g' \
		<$< >$@

dist-hook:
	find $(distdir)/bind -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	mkdir -p $(DESTDIR)$(bindconfdir)
	cp -RT $(srcdir)/bind $(DESTDIR)$(bindconfdir)
