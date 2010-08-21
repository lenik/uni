
SUBDIRS = lib aliases.d

sysconfdir = /etc

dist_sysconf_DATA = \
    bash_aliases

EXTRA_DIST = post-install.sh

install-data-hook:
	$(srcdir)/post-install.sh $(DESTDIR)$(sysconfdir)
