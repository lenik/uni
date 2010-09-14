
include libauto/coolauto.am

setupdir = ${datadir}/setup/${PACKAGE_NAME}

sysconfdir = /etc
bindconfdir = ${sysconfdir}/bind

setup_SCRIPTS = \
    post-install.sh \
    pre-remove.sh

EXTRA_DIST = \
	bind \
	post-install.sh.in \
	pre-remove.sh.in

CLEANFILES = \
	post-install.sh \
	pre-remove.sh

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]bindconfdir[@],$(bindconfdir),g' \
		<$< >$@

dist-hook:
	find $(distdir)/bind -type d -name .svn -exec rm -fR {} \; -prune

install-data-hook:
	mkdir -p $(DESTDIR)$(bindconfdir)
	cp -R $(srcdir)/bind/* $(DESTDIR)$(bindconfdir)
