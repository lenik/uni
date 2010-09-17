
SUBDIRS = lib aliases.d profile.d sudoers.d

sysconfdir = /etc
shsrcdir = ${bindir}

dist_bin_SCRIPTS = \
	eachdir \
	swapox \
	testox

dist_shsrc_DATA = \
	cd-ls \
	ls-which

dist_sysconf_DATA = \
    bash_aliases

dist_pkgdata_SCRIPTS = \
	postinst \
	prerm

install-data-hook:
	@echo "Please run: $(DESTDIR)$(pkgdatadir)/postinst $(DESTDIR)$(sysconfdir)"

uninstall-hook:
	@echo "Please run: $(DESTDIR)$(pkgdatadir)/prerm $(DESTDIR)$(sysconfdir)"

