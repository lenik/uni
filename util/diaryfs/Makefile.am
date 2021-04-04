include libauto/project.am

dist_bin_SCRIPTS = \
    by-date \
    diaryfs

dist_completion_SCRIPTS = \
    completion/diaryfs
    
dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	ln -sn diaryfs ${DESTDIR}${bindir}/dfs
	ln -sn diaryfs ${DESTDIR}${completiondir}/dfs

