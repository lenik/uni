
SUBDIRS = lib aliases.d profile.d

sysconfdir = /etc

dist_bin_SCRIPTS = \
	eachdir \
	tourl \
	textp \
	binaryp

dist_sysconf_DATA = \
    bash_aliases

EXTRA_DIST = post-install.sh pre-remove.sh

install-data-hook:
	$(srcdir)/post-install.sh $(DESTDIR)$(sysconfdir)

uninstall-hook:
	$(srcdir)/pre-remove.sh $(DESTDIR)$(sysconfdir)
