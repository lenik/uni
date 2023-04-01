include libauto/project.am

qlogconfdir = ${pkgdatadir}

dist_bin_SCRIPTS = \
    chtask \
    cooltask_list \
    mtry \
    qlog \
    qlog-console

dist_shlib_SCRIPTS = \
    findcont

dist_qlogconf_DATA = \
    qlog.conf/focus \
    qlog.conf/priority

dist_alias_DATA = \
    cooltask.sh

dist_completion_DATA = \
    completions/cooltask

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_setup_SCRIPTS}

install-data-hook:
	cd ${DESTDIR}${completiondir}; \
        ln -snf cooltask T; \
        ln -snf cooltask T.
