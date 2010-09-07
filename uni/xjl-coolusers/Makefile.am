
include libauto/subst.am

sysconfdir = ${prefix}/etc
sudoersdir = ${sysconfdir}/sudoers.d

dist_bin_SCRIPTS = \
	setminuid

EXTRA_DIST = \
	70.sh 700.sh \
	check-uid.sh  check-gid.sh \
	remove-uid.sh remove-gid.sh \
	post-install.sh \
	pre-remove.sh

install-data-hook:
	@echo "Please run $(srcdir)/post-install.sh $(srcdir) $(DESTDIR)$(sysconfdir) $(DESTDIR)$(sudoersdir)"

uninstall-hook:
	@echo "Please run $(srcdir)/pre-remove.sh   $(srcdir) $(DESTDIR)$(sysconfdir) $(DESTDIR)$(sudoersdir)"
