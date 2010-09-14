
include libauto/coolauto.am

sysconfdir = /etc
sudoersdir = ${sysconfdir}/sudoers.d

dist_bin_SCRIPTS = \
	setminuid

dist_pkgdata_SCRIPTS = \
	70.sh 700.sh \
	check-uid.sh  check-gid.sh \
	remove-uid.sh remove-gid.sh \
	post-install.sh \
	pre-remove.sh

dist_sudoers_DATA = \
	sudoers.d/dev

install-data-hook:
	@echo "Please run $(DESTDIR)$(pkgdatadir)/post-install.sh $(DESTDIR)$(pkgdatadir) $(DESTDIR)$(sysconfdir) $(DESTDIR)$(sudoersdir)"

uninstall-hook:
	@echo "Please run $(DESTDIR)$(pkgdatadir)/pre-remove.sh   $(DESTDIR)$(pkgdatadir) $(DESTDIR)$(sysconfdir) $(DESTDIR)$(sudoersdir)"
