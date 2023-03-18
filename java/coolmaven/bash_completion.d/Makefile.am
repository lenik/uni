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
	ln -snf m2chdir $(DESTDIR)${completiondir}/MPK
	ln -snf m2chdir $(DESTDIR)${completiondir}/mpk
	ln -snf m2chdir $(DESTDIR)${completiondir}/MIN
	ln -snf m2chdir $(DESTDIR)${completiondir}/min
	ln -snf m2chdir $(DESTDIR)${completiondir}/MDP
	ln -snf m2chdir $(DESTDIR)${completiondir}/mdp
	ln -snf m2chdir $(DESTDIR)${completiondir}/mte

