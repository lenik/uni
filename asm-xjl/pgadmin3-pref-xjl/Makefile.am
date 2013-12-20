include libauto/project.am

pgadmin3datadir = ${datadir}/pgadmin3

do_subst += -e 's,@pgadmin3datadir@,${pgadmin3datadir},g'

dist_setup_DATA = \
    ctlSQLBox.patch

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}

# -N: new file for absent.
# -a: always text mode
# -u: unified format
# -r: recursive
ctlSQLBox.patch: orig/settings.ini dest/settings.ini
	diff -Naur orig/settings.ini dest/settings.ini >$@
