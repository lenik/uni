include libauto/project.am

xtoolsdir=/opt/x-tools

do_subst += \
    -e 's,@xtoolsdir@,${xtoolsdir},g'

dist_profile_SCRIPTS= \
    x-tools.sh

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_profile_SCRIPTS} \
    ${dist_setup_SCRIPTS}
