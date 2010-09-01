
include libauto/subst.am

SUBDIRS = test

sysconfdir = /etc
svnuniondir = ${sysconfdir}/svn_union.d
aliasesdir = ${sysconfdir}/bash_aliases.d

dist_bin_SCRIPTS = \
	coolsvn \
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
	coolsvn.1 \
	svn-union.1 \
	svn_union.d.5

dist_svnunion_DATA = \
	svn_union.d/README

dist_aliases_DATA = \
	aliases.d/coolsvn.sh

EXTRA_DIST = svn-union.in
CLEANFILES = svn-union

svn-union: svn-union.in Makefile
	$(do_subst) -e 's,[@]svnuniondir[@],$(svnuniondir),g' \
		<$(srcdir)/svn-union.in >$@
