
include libauto/subst.am

SUBDIRS = test

sysconfdir = /etc
svnuniondir = ${sysconfdir}/svn_union.d

dist_bin_SCRIPTS = \
	svn-apply \
	svn-backup-sync \
	svn-remix \
	svn-ps \
	svn-start-rev \
	svn-show-usedkeywords \
	svn-update-keywords \
	svn-update-executable \
	svn-union

dist_man_MANS = \
	svn-union.1 \
	svn_union.d.5

dist_svnunion_DATA = \
	svn_union.d/README

EXTRA_DIST = svn-union.in
CLEANFILES = svn-union

svn-union: svn-union.in Makefile
	$(do_subst) -e 's,[@]svnuniondir[@],$(svnuniondir),g' \
		<$(srcdir)/svn-union.in >$@
