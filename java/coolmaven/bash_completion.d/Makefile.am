include ../libauto/common.am

dist_completion_DATA = \
    m2chdir \
    mjava \
    newmp

EXTRA_DIST = \
    newmp.in

CLEANFILES = \
    newmp

install-data-hook:
	ln -snf m2chdir $(DESTDIR)${completiondir}/M
	ln -snf m2chdir $(DESTDIR)${completiondir}/m2which

