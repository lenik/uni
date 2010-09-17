
include libauto/coolauto.am

dist_bin_SCRIPTS = \
	setminuid

dist_setup_SCRIPTS = \
	70.sh 700.sh \
	check-uid.sh  check-gid.sh \
	remove-uid.sh remove-gid.sh \
	postinst \
	prerm

dist_sudoers_DATA = \
	sudoers.d/dev

install-data-hook:
	@echo "Please run $(DESTDIR)$(pkgdatadir)/postinst $(DESTDIR)$(pkgdatadir) $(DESTDIR)$(sysconfdir) $(DESTDIR)$(sudoersdir)"

uninstall-hook:
	@echo "Please run $(DESTDIR)$(pkgdatadir)/prerm   $(DESTDIR)$(pkgdatadir) $(DESTDIR)$(sysconfdir) $(DESTDIR)$(sudoersdir)"
