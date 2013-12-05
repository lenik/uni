include libauto/project.am

xtoolsdir=/opt/x-tools

do_subst += \
    -e 's,@xtoolsdir@,${xtoolsdir},g'

dist_profile_SCRIPTS= \
    x-tools.sh

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${setup_SCRIPTS} \
    ${dist_profile_SCRIPTS}
