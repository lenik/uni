
include libauto/subst.am

cachedir = /var/cache/xjl-coolwget

dist_bin_SCRIPTS = \
    urljoin

bin_SCRIPTS = \
    wgetc

dist_man_MANS = \
	urljoin.1 \
	wgetc.1

cache_DATA =

EXTRA_DIST = wgetc.in
CLEANFILES = wgetc

wgetc: wgetc.in Makefile
	$(do_subst) -e 's,[@]cachedir[@],$(cachedir),g' <$(srcdir)/wgetc.in >$@

install-data-hook:
	chmod 2777 $(DESTDIR)$(cachedir)
