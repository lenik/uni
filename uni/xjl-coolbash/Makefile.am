
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

EXTRA_DIST = post-install.sh pre-remove.sh

install-data-hook:
	$(srcdir)/post-install.sh $(DESTDIR)$(sysconfdir)

uninstall-hook:
	$(srcdir)/pre-remove.sh $(DESTDIR)$(sysconfdir)
