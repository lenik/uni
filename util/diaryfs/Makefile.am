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

install-debug:
	sudo ln -snf $$PWD/by-date.in /usr/bin/by-date
	sudo ln -snf $$PWD/diaryfs.in /usr/bin/diaryfs

