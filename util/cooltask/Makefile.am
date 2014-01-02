include libauto/project.am

qlogconfdir = ${pkgdatadir}

dist_bin_SCRIPTS = \
    cooltask_list \
    mtry \
    qlog \
    qlog-console

dist_qlogconf_DATA = \
    qlog.conf/focus \
    qlog.conf/priority

dist_alias_DATA = \
    cooltask.sh

dist_completion_DATA = \
    cooltask

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS} \
    ${dist_bin_SCRIPTS} \
    cooltask_list \
    qlog \
    qlog-console

