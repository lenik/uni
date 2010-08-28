
include libauto/subst.am

install-data-hook:
	$(srcdir)/check-uid.sh 70 99
	$(srcdir)/check-uid.sh 700 999
	$(srcdir)/check-gid.sh 70 99
	$(srcdir)/check-gid.sh 700 999
	$(srcdir)/70.sh
	$(srcdir)/700.sh

uninstall-hook:
	$(srcdir)/remove-uid.sh 70 99
	$(srcdir)/remove-uid.sh 700 999
	$(srcdir)/remove-gid.sh 70 99
	$(srcdir)/remove-gid.sh 700 999
