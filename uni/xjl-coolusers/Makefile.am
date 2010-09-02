
include libauto/subst.am

sysconfdir = ${prefix}/etc
sudoersdir = ${sysconfdir}/sudoers.d

dist_bin_SCRIPTS = \
	setminuid

install-data-hook:
	install -m 440 $(srcdir)/sudoers.d/dev $(DESTDIR)$(sudoersdir)/
	setminuid 800
	$(srcdir)/check-uid.sh $(DESTDIR)$(sysconfdir) 70 99 700 999
	$(srcdir)/check-gid.sh $(DESTDIR)$(sysconfdir) 70 99 700 999
	$(srcdir)/70.sh  $(DESTDIR)$(sysconfdir)
	$(srcdir)/700.sh $(DESTDIR)$(sysconfdir)

uninstall-hook:
	$(srcdir)/remove-uid.sh $(DESTDIR)$(sysconfdir) 70 99 700 999
	$(srcdir)/remove-gid.sh $(DESTDIR)$(sysconfdir) 70 99 700 999
