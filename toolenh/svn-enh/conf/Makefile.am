include ../libauto/common.am

svnconfdir = ${sysconfdir}/subversion/cls/conf

do_subst += -e 's,@svnconfdir@,${svnconfdir},g'

dist_svnconf_DATA = \
    svnserve.conf \
    authz \
    passwd
