
include libauto/subst.am

cachedir = /var/cache/downloads

dist_bin_SCRIPTS = \
    urljoin \
	grepurl

bin_SCRIPTS = \
    wgetc \
	wgetc-url

dist_man_MANS = \
	urljoin.1 \
	grepurl.1 \
	wgetc.1 \
	wgetc-url.1

cache_DATA =

EXTRA_DIST = wgetc.in wgetc-url.in
CLEANFILES = wgetc    wgetc-url

wgetc: wgetc.in Makefile
	$(do_subst) -e 's,[@]cachedir[@],$(cachedir),g' <$(srcdir)/wgetc.in >$@

wgetc-url: wgetc-url.in Makefile
	$(do_subst) -e 's,[@]cachedir[@],$(cachedir),g' <$(srcdir)/wgetc-url.in >$@

install-data-hook:
	chmod 2777 $(DESTDIR)$(cachedir)
