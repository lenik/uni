include libauto/coolauto.am

SUBDIRS = test conf

do_subst += -e 's,[@]svnuniondir[@],$(svnuniondir),g'

svnuniondir = ${sysconfdir}/svn_union.d

dist_bin_SCRIPTS = \
    svn-002 \
    svn-apply \
    svn-backup-sync \
    svn-commit-autover \
    svn-dirty-dirs  \
    svn-export \
    svn-ignore \
    svn-ignore-unversioned \
    svn-isclean \
    svn-log \
    svn-ps \
    svn-remix \
    svn-rename \
    svn-show-usedkeywords \
    svn-start-rev \
    svn-swf \
    svn-unadd \
    svn-union \
    svn-union-install \
    svn-update-executable \
    svn-update-keywords \
    svnpsl \
    svnserve-002 \
    svnadmin-authz-update \
    svnm

dist_man_MANS = \
    coolsvn.1 \
    svn-export.1 \
    svn-rename.1 \
    svn-union.1 \
    svn_union.d.5 \
    svn-swf.1 \
    svnpsl.1

dist_svnunion_DATA = \
    svn_union.d/README

dist_aliases_DATA = \
    coolsvn.sh

aliases_DATA = \
    sw-svn.sh

EXTRA_DIST = \
    svn-union.in \
    svn-union-install.in \
    sw-svn.sh.in

CLEANFILES = \
    svn-union \
    svn-union-install \
    sw-svn.sh

