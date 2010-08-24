
sysconfdir = /etc
sudoersdir = ${sysconfdir}/sudoers.d

dist_sudoers_DATA = \
    keep-hist

install-data-hook:
	cd $(DESTDIR)$(sudoersdir); \
	chmod 440 keep-hist
