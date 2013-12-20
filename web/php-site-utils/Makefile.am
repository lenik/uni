include libauto/project.am

SUBDIRS = php

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

%: %.in Makefile
	$(do_subst) <$< >$@
