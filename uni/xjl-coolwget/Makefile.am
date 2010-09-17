include libauto/coolauto.am

cachedir = /var/cache/downloads
sudoersdir = /etc/sudoers.d

cache_DATA =

# TODO: Don't know how to configure sudoers.
#sudoers_DATA =
#	sudoers.d/wget

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

setup_SCRIPTS = \
	postinst \
	prerm

EXTRA_DIST = \
	postinst.in \
	prerm.in \
	wgetc.in \
	wgetc-url.in

CLEANFILES = \
	postinst \
	prerm \
	wgetc \
	wgetc-url

%: %.in Makefile
	$(do_subst) \
		-e 's,[@]cachedir[@],$(cachedir),g' \
		-e 's,[@]sudoersdir[@],$(sudoersdir),g' \
		<$< >$@

